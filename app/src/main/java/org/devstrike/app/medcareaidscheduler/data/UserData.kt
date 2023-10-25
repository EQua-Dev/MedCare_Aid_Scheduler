package org.devstrike.app.medcareaidscheduler.data

data class UserData(
    val userID: String = "",
    val userRole: String = "",
    val userFirstName: String = "",
    val userLastName: String = "",
    val userDistrictID: String = "",
    val userContactNumber: String = "",
    val userEmail: String = "",
    val userBankName: String = "",
    val userBankAccountNumber: String = "",
    val userAddress: String = "",
    val userGender: String = "",
    val userChangedPassword: Boolean = false,
    val userProvinceID: String = "",
    val userAssignedHouse: String = "",
    val userPasswordChangedDate: String = "",

)
