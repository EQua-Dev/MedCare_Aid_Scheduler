package org.devstrike.app.medcareaidscheduler.data

data class House(
    val houseName: String = "",
    val houseAddress: String = "",
    val houseDistrict: String = "",
    val houseProvince: String = "",
    val houseNoOfClients: String = "",
    val houseID: String = "",
    val houseIsSpecialCare: Boolean = false,
    val houseSpecialCareType: String = "",
    val houseContactPerson: String = "",
    val houseContactNumber: String = "",
    val houseIs3rdParty: Boolean = false,
    val houseNameOfPatients: List<String> = listOf(),
    val houseNecessaryInformation: String = "",
    val houseDateAdded: String = "",
    val houseAddingSupervisor: String = "",
    )
