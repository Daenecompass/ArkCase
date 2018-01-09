package com.armedia.acm.services.dataupdate.model;

import com.armedia.acm.data.converter.LocalDateConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "acm_data_update_executor")
public class AcmDataUpdateExecutorLog
{
    @Id
    @Column(name = "cm_executor_id")
    private String executorId;

    @Column(name = "cm_executed_on")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate executedOn;

    public String getExecutorId()
    {
        return executorId;
    }

    public void setExecutorId(String executorId)
    {
        this.executorId = executorId;
    }

    public LocalDate getExecutedOn()
    {
        return executedOn;
    }

    public void setExecutedOn(LocalDate executedOn)
    {
        this.executedOn = executedOn;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcmDataUpdateExecutorLog that = (AcmDataUpdateExecutorLog) o;
        return Objects.equals(executorId, that.executorId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(executorId);
    }
}