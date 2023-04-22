FROM python:3-alpine
COPY create.sh /usr/local/bin
WORKDIR /usr/local/app
RUN apk add --no-cache git bash
RUN git clone --recursive https://github.com/googleapis/python-pubsub.git
WORKDIR /usr/local/app/python-pubsub/samples/snippets
RUN ls -la && env
ENV PUBSUB_EMULATOR_HOST=pubsub:8085
RUN --mount=type=cache,target=/root/.cache pip install -r requirements.txt
CMD ["create.sh"]