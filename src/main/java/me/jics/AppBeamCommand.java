package me.jics;

import io.micronaut.configuration.picocli.PicocliRunner;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Wait;
import org.apache.beam.sdk.values.PCollection;
import picocli.CommandLine.Command;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static picocli.CommandLine.Option;

@SuppressWarnings("unused")
@Slf4j
@Command(name = "app-beam", description = "...",
        mixinStandardHelpOptions = true)
public class AppBeamCommand implements Runnable {

    @Inject
    AuthDatabaseConfig authDatabaseConfig;

    @Option(names = {"-da", "--dataflow-args"}, description = "parameters you will pass to the runner selected")
    String dataflowArgs;

    public static void main(String[] args) throws Exception {
        int code = PicocliRunner.execute(AppBeamCommand.class, args);
        System.exit(code);
    }

    public void run() {

        AppBeamOptions options = PipelineOptionsFactory
                .fromArgs(dataflowArgs.split(" ")).withValidation()
                .as(AppBeamOptions.class);
        String subscription = options.getInputSubscription();
        Pipeline p = Pipeline.create(options);


        PCollection<String> result = p.
                apply("Pubsub", PubsubIO.readMessagesWithAttributes().fromSubscription(String.format("projects/%s/subscriptions/%s", options.getProjectId(), subscription)))
                .apply("Transform", ParDo.of(new MyTransformer()));

        PCollection<Void> insert = result.apply("Inserting",
                JdbcIO.<String>write()
                        .withDataSourceProviderFn(DataSourceProvider.of(authDatabaseConfig))
                        .withStatement("INSERT INTO person (first_name, last_name) VALUES (?, 'doe')")
                        .withPreparedStatementSetter((element, preparedStatement) -> {
                            log.info("Preparing statement to insert");
                            preparedStatement.setString(1, element);
                        })
                        .withResults()
        );
        result.apply(Wait.on(insert))
                .apply("Selecting", new SomeTransform())
                .apply("PubsubMessaging", ParDo.of(new NextTransformer()));
        p.run();
    }

    @RequiredArgsConstructor
    private static class SomeTransform extends PTransform<PCollection<String>, PCollection<UserData>> {
        private static AuthDatabaseConfig authDatabaseConfig;

        @Override
        public PCollection<UserData> expand(PCollection<String> input) {
            return input.apply(JdbcIO.<String, UserData>readAll()
                    .withDataSourceProviderFn(DataSourceProvider.of(authDatabaseConfig))
                    .withQuery("select first_name, last_name from person where first_name = 'john'")
                    .withParameterSetter((element, preparedStatement) -> {
                        log.info("Preparing statement to select");
                        preparedStatement.setString(1, element.toString());
                    })
                    .withOutputParallelization(false)
                    .withRowMapper(resultSet -> {
                        log.info("Result of the select");
                        return new UserData(resultSet.getString(1), "");
                    })
                    .withCoder(SerializableCoder.of(UserData.class))
            );
        }
    }

    private static class NextTransformer extends DoFn<UserData, PubsubMessage> {
        @SuppressWarnings("unused")
        @ProcessElement
        public void processElement(@Element UserData element, ProcessContext c) {
            log.info("NextTransformer");
            c.output(new PubsubMessage(element.getName().getBytes(), Map.of("test", "test")));
        }
    }

    @Slf4j
    private static class MyTransformer extends DoFn<PubsubMessage, String> {

        @ProcessElement
        public void processElement(@Element PubsubMessage p, OutputReceiver<String> o) {
            log.info("MyTransformer");
            o.output(new String(p.getPayload(), StandardCharsets.UTF_8));
        }
    }
}
