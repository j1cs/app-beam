#!/usr/bin/env bash
python publisher.py my-gcp-project create legacy-topic
python subscriber.py my-gcp-project create legacy-topic legacy-subscription
python publisher.py my-gcp-project create main-topic
python subscriber.py my-gcp-project create main-topic main-subscription