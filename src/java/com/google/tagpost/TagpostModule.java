package com.google.tagpost;

import com.google.inject.AbstractModule;
import com.google.tagpost.spanner.DataService;
import com.google.tagpost.spanner.SpannerService;

public class TagpostModule extends AbstractModule {
  @Override
  /** Bind the service to implementation class */
  protected void configure() {
    bind(DataService.class).to(SpannerService.class);
  }
}
