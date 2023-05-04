package com.digitalstack.logistics.configuration;

import com.digitalstack.logistics.company_manager.CompanyManager;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class ActuatorConfig implements InfoContributor
{
    private final CompanyManager companyManager;

    public ActuatorConfig(CompanyManager companyManager)
    {
        this.companyManager = companyManager;
    }

    @Override
    public void contribute(Info.Builder builder)
    {
        builder.withDetail("Current Date", companyManager.getCurrentDate());
        builder.withDetail("Company profit", companyManager.getCompanyProfit());
    }
}
