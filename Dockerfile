FROM ubuntu:latest
LABEL authors="dsi"

ENTRYPOINT ["top", "-b"]
