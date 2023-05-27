package de.spicom.zaster.endpoints.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
public class Currency {
    @Nullable
    private String id;
    private String currencyCode;
    private String name;
}
