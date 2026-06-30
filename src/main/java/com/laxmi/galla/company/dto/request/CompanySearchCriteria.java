package com.laxmi.galla.company.dto.request;

import java.time.Instant;

public record CompanySearchCriteria(  // Search by company name, PAN, or phone
                                      String searchTerm,

                                      // Exact filters
                                      String panNumber,
                                      String phoneNo,

                                      // Date range (AuditableEntity)
                                      Instant createdFrom,
                                      Instant createdTo) {
}
