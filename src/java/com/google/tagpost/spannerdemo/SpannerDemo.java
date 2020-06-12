package com.google.tagpost.spannerdemo;

import com.google.cloud.ByteArray;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demo program for Spanner.
 * <p>
 * Inserts a row containing the current user name and timestamp.
 */
public class SpannerDemo {

  public static void main(String args[]) throws SpannerException {
    System.out.println("Spanner Demo");

    final String userName = System.getProperty("user.name");
    final Instant timestamp = Instant.now();

    SpannerOptions spannerOptions = SpannerOptions.newBuilder().build();
    Spanner spanner = spannerOptions.getService();
    DatabaseClient databaseClient = spanner
        .getDatabaseClient(DatabaseId.of("testing-bigtest", "tagpost", "test"));
    Mutation mutation = Mutation.newInsertBuilder("Table1")
        .set("NoticeKey").to(String.format("%s posted at %d", userName, timestamp.getEpochSecond()))
        .set("Project").to(1L)
        .set("User").to(userName)
        .set("Info").to(ByteArray.copyFrom("abcd"))
        .build();
    Timestamp writeTimestamp = databaseClient.write(ImmutableList.of(mutation));
    System.out.printf("Write timestamp: %s\n", ZonedDateTime
        .ofInstant(Instant.ofEpochSecond(writeTimestamp.getSeconds(), writeTimestamp.getNanos()),
            ZoneId.systemDefault()).format(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }
}
