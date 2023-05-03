# syntax=docker/dockerfile:1

#FROM maven:3.8.3-openjdk-17
FROM maven:3.6.3-jdk-8

WORKDIR /app

COPY . .
RUN mvn package
