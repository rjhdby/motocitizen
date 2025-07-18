package motocitizen.content

import motocitizen.content.volunteer.Volunteer
import java.util.concurrent.ConcurrentHashMap

object VolunteersController {
    val volunteers: ConcurrentHashMap<Int, Volunteer> = ConcurrentHashMap()
    fun addVolunteers(newVolunteers: List<Volunteer>) {
        newVolunteers.forEach { volunteers[it.id] = it }
    }
}