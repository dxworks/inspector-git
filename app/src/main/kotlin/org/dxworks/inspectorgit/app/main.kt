package org.dxworks.inspectorgit.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan("org.dxworks.inspectorgit.persistence")
@ComponentScan(basePackages = ["org.dxworks.inspectorgit"])
@EnableJpaRepositories(basePackages = ["org.dxworks.inspectorgit.persistence"])
@SpringBootApplication
class InspectorgitApplication

fun main(args: Array<String>) {
    runApplication<InspectorgitApplication>(*args)
}