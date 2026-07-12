# --- Config ---
$resultsFile = "sample-playwright-results.json"
$uploadApiUrl = "http://localhost:8080/test-outcome/api/test-runs/upload"
$authApiUrl = "http://localhost:8080/test-outcome/api/auth/login"

# Credentials for API authentication
$username = "frik"
$password = "Password1"

# Metadata
$organizationId = "6a226bae704ada3e7cc8ba49"
$framework = "playwright"
$format = "JSON"
$project = "playwright-sample-json"
$branch = "main"
$commitId = "xyz123"
$environment = "dev"
$source = "script"


# --- Step 1: Authenticate and get token ---
Write-Host "Authenticating with API..."
try {
    $loginRequest = @{
        username = $username
        password = $password
    } | ConvertTo-Json

    $authResponse = Invoke-WebRequest -Uri $authApiUrl -Method Post -Body $loginRequest -ContentType "application/json"
    $authData = $authResponse.Content | ConvertFrom-Json
    $token = $authData.token

    Write-Host "Authentication successful!"
    Write-Host "Token: $($token.Substring(0, 20))..."
}
catch {
    Write-Error "Authentication failed"
    Write-Error $_
    exit 1
}


# --- Step 2: Build API payload ---
$resultsContent = Get-Content $resultsFile -Raw | ConvertFrom-Json

$requestJson = @{
	organizationId = $organizationId
    framework   = $framework
	format      = $format    
    project     = $project
    branch      = $branch
    commitId    = $commitId
    environment = $environment
    source      = $source
} | ConvertTo-Json -Depth 100

$form = @{
    request = $requestJson
    report = Get-Item -Path $resultsFile
}

Write-Host "API payload constructed."


# --- Step 3: Call REST API with Bearer token ---
Write-Host "Uploading results to API..."

try {
    # Build multipart form data with proper Content-Type for JSON field
    $LF = "`r`n"
    $boundary = [System.Guid]::NewGuid().ToString()
    $bodyLines = @()
    
    # Add request field with application/json content type
    $bodyLines += "--$boundary"
    $bodyLines += 'Content-Disposition: form-data; name="request"'
    $bodyLines += "Content-Type: application/json"
    $bodyLines += ""
    $bodyLines += $requestJson
    
    # Add report file field
    $bodyLines += "--$boundary"
    $bodyLines += 'Content-Disposition: form-data; name="report"; filename="sample-playwright-results.json"'
    $bodyLines += "Content-Type: application/json"
    $bodyLines += ""
    $bodyLines += (Get-Content $resultsFile -Raw)
    $bodyLines += "--$boundary--"
    
    $body = $bodyLines -join $LF
    
    # Add Authorization header with Bearer token
    $response = Invoke-WebRequest -Uri $uploadApiUrl -Method Post -Body $body -ContentType "multipart/form-data; boundary=$boundary" -Headers @{"Authorization" = "Bearer $token"}

    Write-Host "Upload successful!"
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 5
}
catch {
    Write-Error "Failed to upload results"
    Write-Error $_
}
