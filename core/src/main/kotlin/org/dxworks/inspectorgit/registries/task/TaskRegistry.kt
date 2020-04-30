package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.compassmetrics.Period
import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.model.task.Task
import org.dxworks.inspectorgit.registries.AbstractRegistry
import java.time.LocalTime


class TaskRegistry : AbstractRegistry<Task, String>() {
    override fun getID(entity: Task) = entity.id

    val allDetailedTasks: List<DetailedTask>
        get() = all.filter { it is DetailedTask }.map { it as DetailedTask }

    val period: Period
        get() = Period(
                allDetailedTasks.minBy { it.created }!!.created.with(LocalTime.MIN),
                allDetailedTasks.maxBy { it.updated }!!.updated.with(LocalTime.MAX)
        )
}