param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$BaseUrl = $BaseUrl.TrimEnd("/")

function Invoke-Api {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$Method,
        [hashtable]$Body
    )

    $parameters = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
    }
    if ($null -ne $Body) {
        $parameters.ContentType = "application/json"
        $parameters.Body = $Body | ConvertTo-Json -Depth 10
    }
    Invoke-RestMethod @parameters
}

function Assert-Message {
    param(
        [string]$Actual,
        [string]$Expected
    )

    if ($Actual -ne $Expected) {
        throw "Expected '$Expected' but received '$Actual'."
    }
}

$existingTypes = Invoke-Api -Path "/api/service-types/all" -Method "GET"
$existingStalls = Invoke-Api -Path "/api/stalls/all" -Method "GET"
$existingVendors = Invoke-Api -Path "/api/vendors/all" -Method "GET"
$existingOrganizers = Invoke-Api -Path "/api/organizers/all" -Method "GET"
$existingBookings = Invoke-Api -Path "/api/event-bookings/all" -Method "GET"
$existingRecordCount = @($existingTypes).Count + @($existingStalls).Count + @($existingVendors).Count
    + @($existingOrganizers).Count + @($existingBookings).Count
if ($existingRecordCount -ne 0) {
    throw "Seed data requires an empty event-management database. No records were created."
}

$serviceTypeNames = @("Catering", "Decoration", "Sound and Lighting", "Photography")
foreach ($name in $serviceTypeNames) {
    Assert-Message (Invoke-Api -Path "/api/service-types/save" -Method "POST" -Body @{ name = $name }) "Service type saved successfully"
}
$serviceTypes = Invoke-Api -Path "/api/service-types/all" -Method "GET"
$serviceTypeIds = @{}
foreach ($serviceType in $serviceTypes) {
    $serviceTypeIds[$serviceType.name] = $serviceType.id
}

for ($number = 1; $number -le 4; $number++) {
    Assert-Message (Invoke-Api -Path "/api/stalls/save" -Method "POST" -Body @{
        stallNumber = $number
        name = "Stall $number"
    }) "Stall saved successfully"
}
$stalls = Invoke-Api -Path "/api/stalls/all" -Method "GET"
$stallIds = @{}
foreach ($stall in $stalls) {
    $stallIds[[string]$stall.stallNumber] = $stall.id
}

$vendorData = @(
    @{
        vendorName = "Kigali Catering"
        contactPerson = "Alex"
        phone = "+250788100001"
        stall = @{ id = $stallIds["1"] }
        serviceTypes = @(
            @{ id = $serviceTypeIds["Catering"] }
            @{ id = $serviceTypeIds["Decoration"] }
        )
    }
    @{
        vendorName = "Sound House"
        contactPerson = "Benoit"
        phone = "+250788100002"
        stall = @{ id = $stallIds["2"] }
        serviceTypes = @(@{ id = $serviceTypeIds["Sound and Lighting"] })
    }
    @{
        vendorName = "Photo Studio"
        contactPerson = "Claire"
        phone = "+250788100003"
        stall = @{ id = $stallIds["3"] }
        serviceTypes = @(@{ id = $serviceTypeIds["Decoration"] })
    }
)
foreach ($vendor in $vendorData) {
    Assert-Message (Invoke-Api -Path "/api/vendors/save" -Method "POST" -Body $vendor) "Vendor saved successfully"
}
$vendors = Invoke-Api -Path "/api/vendors/all" -Method "GET"
$vendorIds = @{}
foreach ($vendor in $vendors) {
    $vendorIds[$vendor.vendorName] = $vendor.id
}

$organizerData = @(
    @{ firstName = "Aline"; lastName = "Gatete"; organizerCode = "ORG-021"; phone = "+250788200001"; email = "aline@example.com" }
    @{ firstName = "Jean"; lastName = "Gatete"; organizerCode = "ORG-022"; phone = "+250788200002"; email = "jean@example.com" }
    @{ firstName = "Maya"; lastName = "Niyonsenga"; organizerCode = "ORG-023"; phone = "+250788200003"; email = "maya@example.com" }
    @{ firstName = "Eric"; lastName = "Uwimana"; organizerCode = "ORG-024"; phone = "+250788200004"; email = "eric@example.com" }
    @{ firstName = "Diane"; lastName = "Kamanzi"; organizerCode = "ORG-025"; phone = "+250788200005"; email = "diane@example.com" }
)
foreach ($organizer in $organizerData) {
    Assert-Message (Invoke-Api -Path "/api/organizers/save" -Method "POST" -Body $organizer) "Organizer saved successfully"
}
$organizers = Invoke-Api -Path "/api/organizers/all" -Method "GET"
$organizerIds = @{}
foreach ($organizer in $organizers) {
    $organizerIds[$organizer.organizerCode] = $organizer.id
}

$bookingPlan = @(
    @{ name = "Booking 01"; vendor = "Kigali Catering"; organizer = "ORG-021"; targetDate = "2026-10-20"; status = "ACCEPTED" }
    @{ name = "Booking 02"; vendor = "Kigali Catering"; organizer = "ORG-021"; targetDate = "2026-10-20"; status = "DECLINED" }
    @{ name = "Booking 03"; vendor = "Kigali Catering"; organizer = "ORG-021"; targetDate = "2026-10-11"; status = "DELIVERED" }
    @{ name = "Booking 04"; vendor = "Sound House"; organizer = "ORG-022"; targetDate = "2026-10-25"; status = "REQUESTED" }
    @{ name = "Booking 05"; vendor = "Sound House"; organizer = "ORG-022"; targetDate = "2026-11-05"; status = "DELIVERED" }
    @{ name = "Booking 06"; vendor = "Sound House"; organizer = "ORG-023"; targetDate = "2026-11-06"; status = "ACCEPTED" }
    @{ name = "Booking 07"; vendor = "Photo Studio"; organizer = "ORG-023"; targetDate = "2026-11-07"; status = "REQUESTED" }
    @{ name = "Booking 08"; vendor = "Photo Studio"; organizer = "ORG-024"; targetDate = "2026-10-28"; status = "DECLINED" }
    @{ name = "Booking 09"; vendor = "Photo Studio"; organizer = "ORG-024"; targetDate = "2026-10-29"; status = "REQUESTED" }
    @{ name = "Booking 10"; vendor = "Kigali Catering"; organizer = "ORG-025"; targetDate = "2026-11-10"; status = "ACCEPTED" }
)
$savedBookingPlans = @()
for ($index = 0; $index -lt $bookingPlan.Count; $index++) {
    $plan = $bookingPlan[$index]
    $temporaryDate = (Get-Date).Date.AddDays($index + 1).ToString("yyyy-MM-dd")
    $body = @{
        eventDate = $temporaryDate
        eventName = $plan.name
        guests = 80
        status = "DECLINED"
        organizer = @{ id = $organizerIds[$plan.organizer] }
        vendor = @{ id = $vendorIds[$plan.vendor] }
    }
    Assert-Message (Invoke-Api -Path "/api/event-bookings/save" -Method "POST" -Body $body) "Event booking saved successfully"
    $savedBookingPlans += @{ plan = $plan; temporaryDate = $temporaryDate }
}

$bookings = Invoke-Api -Path "/api/event-bookings/all" -Method "GET"
$bookingIds = @{}
foreach ($booking in $bookings) {
    $bookingIds[$booking.eventName] = $booking.id
}

foreach ($saved in $savedBookingPlans) {
    $plan = $saved.plan
    $booking = $bookings | Where-Object { $_.eventName -eq $plan.name } | Select-Object -First 1
    $body = @{
        eventDate = $plan.targetDate
        eventName = $plan.name
        guests = $booking.guests
        status = $plan.status
        organizer = @{ id = $booking.organizer.id }
        vendor = @{ id = $booking.vendor.id }
    }
    Assert-Message (Invoke-Api -Path "/api/event-bookings/update/$($booking.id)" -Method "PUT" -Body $body) "Event booking updated successfully"

    $nextStatuses = switch ($plan.status) {
        "ACCEPTED" { @("ACCEPTED") }
        "DELIVERED" { @("ACCEPTED", "DELIVERED") }
        "DECLINED" { @("DECLINED") }
        default { @() }
    }
    foreach ($status in $nextStatuses) {
        Assert-Message (Invoke-Api -Path "/api/event-bookings/$($booking.id)/status?status=$status" -Method "PATCH") "Event booking status updated successfully"
    }
}

Write-Output "Seed data created successfully through $BaseUrl."
Write-Output "Created $($serviceTypes.Count) service types, $($stalls.Count) stalls, $($vendors.Count) vendors, $($organizers.Count) organizers, and $($bookings.Count) event bookings."
Write-Output "Service type IDs:"
foreach ($serviceType in $serviceTypes) { Write-Output "  $($serviceType.name): $($serviceType.id)" }
Write-Output "Stall IDs:"
foreach ($stall in $stalls) { Write-Output "  Stall $($stall.stallNumber): $($stall.id)" }
Write-Output "Vendor IDs:"
foreach ($vendor in $vendors) { Write-Output "  $($vendor.vendorName): $($vendor.id)" }
Write-Output "Organizer IDs:"
foreach ($organizer in $organizers) { Write-Output "  $($organizer.organizerCode): $($organizer.id)" }
Write-Output "Event booking IDs:"
foreach ($booking in $bookings) { Write-Output "  $($booking.eventName): $($booking.id)" }
