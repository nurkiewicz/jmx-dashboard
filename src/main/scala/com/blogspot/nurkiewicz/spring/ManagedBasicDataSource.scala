package com.blogspot.nurkiewicz.spring

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.jmx.export.annotation.{ManagedAttribute, ManagedResource}

@ManagedResource
class ManagedBasicDataSource extends BasicDataSource {

    @ManagedAttribute override def getNumActive = super.getNumActive
    @ManagedAttribute override def getNumIdle = super.getNumIdle
    @ManagedAttribute def getNumOpen = getNumActive + getNumIdle
    @ManagedAttribute override def getMaxActive: Int= super.getMaxActive
    @ManagedAttribute override def setMaxActive(maxActive: Int) {
        super.setMaxActive(maxActive)
    }

    @ManagedAttribute override def getMaxIdle = super.getMaxIdle
    @ManagedAttribute override def setMaxIdle(maxIdle: Int) {
        super.setMaxIdle(maxIdle)
    }

    @ManagedAttribute override def getMinIdle = super.getMinIdle
    @ManagedAttribute override def setMinIdle(minIdle: Int) {
        super.setMinIdle(minIdle)
    }

    @ManagedAttribute override def getMaxWait = super.getMaxWait
    @ManagedAttribute override def setMaxWait(maxWait: Long) {
        super.setMaxWait(maxWait)
    }

    @ManagedAttribute override def getUrl = super.getUrl
    @ManagedAttribute override def getUsername = super.getUsername
}