rootProject.name = "autotrading"
include("coin-spot")
include("coin-futures")
include("infrastructure")
include("infrastructure:domain")
findProject(":infrastructure:domain")?.name = "domain"
include("infrastructure:usecase")
findProject(":infrastructure:usecase")?.name = "usecase"
include("infrastructure:persistence")
findProject(":infrastructure:persistence")?.name = "persistence"
include("infrastructure:client")
findProject(":infrastructure:client")?.name = "client"
include("infrastructure:common")
findProject(":infrastructure:common")?.name = "common"
include("infrastructure:auth")
findProject(":infrastructure:auth")?.name = "auth"
include("view-dashboard")
