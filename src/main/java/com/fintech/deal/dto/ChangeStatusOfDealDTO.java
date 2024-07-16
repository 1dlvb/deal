package com.fintech.deal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealStatus;
import com.fintech.deal.model.DealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "description", "agreement_number", "agreement_date", "agreement_start_dt",
        "availability_date", "type", "status", "sum", "close_dt"})
public class ChangeStatusOfDealDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("agreement_number")
    private String agreementNumber;

    @JsonProperty("agreement_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate agreementDate;

    @JsonProperty("agreement_start_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime agreementStartDt;

    @JsonProperty("availability_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate availabilityDate;

    @JsonProperty("type")
    private DealType type;

    @JsonProperty("status")
    private DealStatus status;

    @JsonProperty("sum")
    private BigDecimal sum;

    @JsonProperty("close_dt")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime closeDt;

    public static Deal fromDTO(ChangeStatusOfDealDTO changeStatusOfDealDTO) {

        return Deal.builder()
                .id(changeStatusOfDealDTO.id)
                .description(changeStatusOfDealDTO.description)
                .agreementNumber(changeStatusOfDealDTO.agreementNumber)
                .agreementDate(changeStatusOfDealDTO.agreementDate)
                .agreementStartDt(changeStatusOfDealDTO.agreementStartDt)
                .availabilityDate(changeStatusOfDealDTO.availabilityDate)
                .status(changeStatusOfDealDTO.status)
                .type(changeStatusOfDealDTO.type)
                .sum(changeStatusOfDealDTO.sum)
                .closeDt(changeStatusOfDealDTO.closeDt)
                .isActive(true)
                .build();
    }

    public static ChangeStatusOfDealDTO toDTO(Deal deal) {
        return ChangeStatusOfDealDTO.builder()
                .id(deal.getId())
                .description(deal.getDescription())
                .agreementNumber(deal.getAgreementNumber())
                .agreementDate(deal.getAgreementDate())
                .agreementStartDt(deal.getAgreementStartDt())
                .availabilityDate(deal.getAvailabilityDate())
                .type(deal.getType())
                .status(deal.getStatus())
                .sum(deal.getSum())
                .closeDt(deal.getCloseDt())
                .build();
    }

}
