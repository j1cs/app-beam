package me.jics;

import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.StreamingOptions;

public interface AppBeamOptions extends StreamingOptions, PipelineOptions {
    String getInputSubscription();

    void setInputSubscription(String inputSubscription);

    String getOutputTopic();


    void setOutputTopic(String outputTopic);

    String getProjectId();

    void setProjectId(String projectId);
}
