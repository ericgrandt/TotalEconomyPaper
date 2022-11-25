package com.ericgrandt.totaleconomy.data.dto;

import java.math.BigDecimal;

public record JobRewardDto(
    String id,
    String jobId,
    String jobActionId,
    int currencyId,
    String objectId,
    BigDecimal money,
    int experience
) {

}
