// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.spanner.pgadapter.utils;

import com.google.api.core.InternalApi;
import com.google.cloud.spanner.ErrorCode;
import com.google.cloud.spanner.SpannerExceptionFactory;
import com.google.cloud.spanner.pgadapter.statements.CopyStatement.Format;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.csv.CSVFormat;

/** Common interface for parsers that implement one or more of the PostgreSQL COPY formats. */
@InternalApi
public interface CopyInParser {
  /**
   * Creates a {@link CopyInParser} for the given format. The csvFormat argument is only required
   * for non-binary formats.
   */
  static CopyInParser create(
      Format format, @Nullable CSVFormat csvFormat, PipedInputStream inputStream, boolean hasHeader)
      throws IOException {
    switch (format) {
      case TEXT:
      case CSV:
        return new CsvCopyParser(csvFormat, inputStream, hasHeader);
      case BINARY:
        return new BinaryCopyParser(inputStream);
      default:
        throw SpannerExceptionFactory.newSpannerException(
            ErrorCode.INVALID_ARGUMENT, "Unsupported COPY format: " + format);
    }
  }

  /** Returns an iterator of COPY records. */
  Iterator<CopyRecord> iterator();

  /** Closes this parser and releases any resources associated with it. */
  void close() throws IOException;
}
