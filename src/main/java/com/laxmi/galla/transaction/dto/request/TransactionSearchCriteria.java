package com.laxmi.galla.transaction.dto.request;

import java.time.Instant;

public record TransactionSearchCriteria(  // Search by transaction name, PAN, or phone
                                      String searchTerm,

                                      // Exact filters
                                      String panNumber,
                                      String phoneNo,

                                      // Date range (AuditableEntity)
                                      Instant createdFrom,
                                      Instant createdTo) {
}
