
$url = "http://localhost:9191/api/department/add"
$token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmNkQGdtYWlsLmNvbSIsImlhdCI6MTc3MzUwNjM5NywiZXhwIjoxNzczNTA3Mjk3fQ.zwuV9oynABUBvXAo-uTKpwdbh-Rv208kb7ZmaQ82xWQvSdMXSn6AQKgHn1NMfEIaKh0tqZT4GhiC9bXCOzBfAw"

$departments = @("Engineering", "Marketing", "HR", "Finance", "Operations","IT Support")

for($i=1; $i -le 6: $i++){
$body =@{
	departmentName = $departments[$i-1]
	headCount = (Get-Random -Minimum 20 -Maximum 100)
} | ConvertTo-Json

	try{
	    $response = Invoke-RestMethod -Uri $url `
	        -Method GET `
	        -Headers @{ Authorization = "Bearer $token"; "Content-Type" = "application/json" } `
	        -Body $body

	        Write-Host "[$i/6] SUCCESS" -ForegroundColor Green
	        Write-Host "    Response: $)$response | ConvertTo-Json - Compress)" -ForegroundColor Cyan
	}catch{
	        Write-Host "[$i/6] FAILED - $($_.Exception.Message)" -ForegroundColor Red
	}
}
Write-Host "`nDone! 6 requests sent." -ForegroundColor Yellow

##############################################################
##"http://localhost:9191/api/department/add"
1..6 | ForEach-Object {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:9191/api/department/add" -Method POST -Headers @{ Authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmNkQGdtYWlsLmNvbSIsImlhdCI6MTc3MzUwNzU3NSwiZXhwIjoxNzczNTA4NDc1fQ.MjJkTEro_m8IAs3nFUeihxnSgaRliiGbsSgECthwEPkq0wwXdymjEUJh9ee3bmzZ2_gio1JaCCh30Ypw7LLIjw" ; "Content-Type" = "application/json" } -Body ( @{ departmentName = "Departments $_"; headCount = (Get-Random -Minimum 20 -Maximum 100)} | ConvertTo-Json );
        Write-Host "[$_/6] SUCCESS" -ForegroundColor Green;
        Write-Host "    Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "[$_/6] FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
}

##"http://localhost:9191/api/employee/add"
1..20 | ForEach-Object {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:9191/api/employee/add" -Method POST -Headers @{ Authorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhYmNkQGdtYWlsLmNvbSIsImlhdCI6MTc3MzUwNzU3NSwiZXhwIjoxNzczNTA4NDc1fQ.MjJkTEro_m8IAs3nFUeihxnSgaRliiGbsSgECthwEPkq0wwXdymjEUJh9ee3bmzZ2_gio1JaCCh30Ypw7LLIjw" ; "Content-Type" = "application/json" } -Body ( @{ firstName = "FirstName $_"; lastName = "LastName $_"; salary = (Get-Random -Minimum 20000 -Maximum 100000); departmentId = (Get-Random -Minimum 2 -Maximum 7)} | ConvertTo-Json );
        Write-Host "[$_/20] SUCCESS" -ForegroundColor Green;
        Write-Host "    Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "[$_/20] FAILED - $($_.Exception.Message)" -ForegroundColor Red
    }
}