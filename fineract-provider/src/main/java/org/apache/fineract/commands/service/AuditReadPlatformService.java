/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.commands.service;

import java.util.List;
import org.apache.fineract.commands.data.AuditData;
import org.apache.fineract.commands.data.request.AuditRequest;
import org.apache.fineract.commands.data.AuditSearchData;
import org.apache.fineract.infrastructure.core.data.PaginationParameters;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.security.utils.SQLBuilder;

public interface AuditReadPlatformService {

    List<AuditData> retrieveAuditEntries(SQLBuilder extraCriteria, boolean includeJson);

    Page<AuditData> retrievePaginatedAuditEntries(SQLBuilder extraCriteria, boolean includeJson, PaginationParameters parameters);

    List<AuditData> retrieveAllEntriesToBeChecked(SQLBuilder extraCriteria, boolean includeJson);

    AuditData retrieveAuditEntry(Long auditId);

    AuditSearchData retrieveSearchTemplate(String useType);

    default SQLBuilder getExtraCriteria(AuditRequest auditRequest) {
        SQLBuilder extraCriteria = new SQLBuilder();
        extraCriteria.addNonNullCriteria("aud.action_name = ", auditRequest.getActionName());
        if (auditRequest.getEntityName() != null) {
            extraCriteria.addCriteria("aud.entity_name like", auditRequest.getEntityName() + "%");
        }
        extraCriteria.addNonNullCriteria("aud.resource_id = ", auditRequest.getResourceId());
        extraCriteria.addNonNullCriteria("aud.maker_id = ", auditRequest.getMakerId());
        extraCriteria.addNonNullCriteria("aud.checker_id = ", auditRequest.getCheckerId());
        if (auditRequest.getMakerDateTimeFrom() != null) {
            extraCriteria.addSubOperation((SQLBuilder criteria) -> {
                criteria.addNonNullCriteria("aud.made_on_date >= ", auditRequest.getMakerDateTimeFrom(),
                        SQLBuilder.WhereLogicalOperator.NONE);
                criteria.addNonNullCriteria("aud.made_on_date_utc >= ", auditRequest.getMakerDateTimeFrom(),
                        SQLBuilder.WhereLogicalOperator.OR);
            });
        }
        if (auditRequest.getMakerDateTimeTo() != null) {
            extraCriteria.addSubOperation((SQLBuilder criteria) -> {
                criteria.addNonNullCriteria("aud.made_on_date <= ", auditRequest.getMakerDateTimeTo(),
                        SQLBuilder.WhereLogicalOperator.NONE);
                criteria.addNonNullCriteria("aud.made_on_date_utc <= ", auditRequest.getMakerDateTimeTo(),
                        SQLBuilder.WhereLogicalOperator.OR);
            });
        }
        if (auditRequest.getCheckerDateTimeFrom() != null) {
            extraCriteria.addSubOperation((SQLBuilder criteria) -> {
                criteria.addNonNullCriteria("aud.checked_on_date >= ", auditRequest.getCheckerDateTimeFrom(),
                        SQLBuilder.WhereLogicalOperator.NONE);
                criteria.addNonNullCriteria("aud.checked_on_date_utc >= ", auditRequest.getCheckerDateTimeFrom(),
                        SQLBuilder.WhereLogicalOperator.OR);
            });
        }
        if (auditRequest.getCheckerDateTimeTo() != null) {
            extraCriteria.addSubOperation((SQLBuilder criteria) -> {
                criteria.addNonNullCriteria("aud.checked_on_date <= ", auditRequest.getCheckerDateTimeTo(),
                        SQLBuilder.WhereLogicalOperator.NONE);
                criteria.addNonNullCriteria("aud.checked_on_date_utc <= ", auditRequest.getCheckerDateTimeTo(),
                        SQLBuilder.WhereLogicalOperator.OR);
            });
        }
        extraCriteria.addNonNullCriteria("aud.status = ", auditRequest.getStatus());
        return extraCriteria;
    }
}
