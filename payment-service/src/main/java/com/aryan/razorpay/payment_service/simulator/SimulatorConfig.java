package com.aryan.razorpay.payment_service.simulator;

import com.aryan.razorpay.common_lib.enums.ChaosMode;
import com.aryan.razorpay.common_lib.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
public class SimulatorConfig {

    private Integer pollIntervalMs = 2000;
    private ChaosMode chaosMode = ChaosMode.NORMAL;
    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    public MethodSimulatorConfig configFor(PaymentMethod paymentMethod) {
        return methods.getOrDefault(paymentMethod.name(), new MethodSimulatorConfig());
    }

    @Getter
    @Setter
    public static class MethodSimulatorConfig {
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;

    }
}
