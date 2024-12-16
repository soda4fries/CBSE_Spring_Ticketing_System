# PowerShell Test Script for Ticket System

# Colors for output
$Green = [System.ConsoleColor]::Green
$Red = [System.ConsoleColor]::Red
$Blue = [System.ConsoleColor]::Blue
$White = [System.ConsoleColor]::White

# Base URL
$BaseUrl = "http://localhost:8080/api/tickets"

# Test counter
$TestsPassed = 0
$TestsFailed = 0

# Function to print test results
function Write-TestResult {
    param (
        [string]$TestName,
        [bool]$Success,
        [string]$Response
    )
    
    if ($Success) {
        Write-Host "✓ $TestName passed" -ForegroundColor $Green
        $script:TestsPassed++
    } else {
        Write-Host "✗ $TestName failed" -ForegroundColor $Red
        Write-Host "Response: $Response" -ForegroundColor $Red
        $script:TestsFailed++
    }
}

# Function to make HTTP requests
function Invoke-TicketRequest {
    param (
        [string]$Uri,
        [string]$Method = "GET",
        [string]$Body,
        [hashtable]$Headers = @{"Content-Type" = "application/json"}
    )
    
    try {
        if ($Body) {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Body $Body -Headers $Headers -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Uri -Method $Method -Headers $Headers -ErrorAction Stop
        }
        return @{
            Success = $true
            Response = $response
        }
    } catch {
        return @{
            Success = $false
            Response = $_.Exception.Message
        }
    }
}

Write-Host "Starting Ticket System API Tests..." -ForegroundColor $Blue
Write-Host

# Test 1: Create a ticket
Write-Host "Test 1: Creating a ticket..."
$createResult = Invoke-TicketRequest -Uri "$BaseUrl`?title=Test%20Ticket&description=Test%20Description" -Method "POST"
$ticketId = $createResult.Response.id
Write-TestResult -TestName "Create Ticket" -Success $createResult.Success -Response $createResult.Response

# Test 2: Get the created ticket
Write-Host "`nTest 2: Getting the ticket..."
$getResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId"
Write-TestResult -TestName "Get Ticket" -Success $getResult.Success -Response $getResult.Response

# Test 3: Update the ticket
Write-Host "`nTest 3: Updating the ticket..."
$updateBody = @{
    id = $ticketId
    title = "Updated Ticket"
    description = "Updated Description"
} | ConvertTo-Json
$updateResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId" -Method "PUT" -Body $updateBody
Write-TestResult -TestName "Update Ticket" -Success $updateResult.Success -Response $updateResult.Response

# Test 4: Assign the ticket
Write-Host "`nTest 4: Assigning the ticket..."
$assignResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/assign?userId=agent1" -Method "PUT"
Write-TestResult -TestName "Assign Ticket" -Success $assignResult.Success -Response $assignResult.Response

# Test 5: Add a reply
Write-Host "`nTest 5: Adding a reply..."
$replyResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/replies?content=Test%20Reply" -Method "POST"
$replyId = $replyResult.Response.id
Write-TestResult -TestName "Add Reply" -Success $replyResult.Success -Response $replyResult.Response

# Test 6: Add a nested reply
Write-Host "`nTest 6: Adding a nested reply..."
$nestedReplyResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/replies?content=Nested%20Reply&parentReplyId=$replyId" -Method "POST"
Write-TestResult -TestName "Add Nested Reply" -Success $nestedReplyResult.Success -Response $nestedReplyResult.Response

# Test 7: Edit reply
Write-Host "`nTest 7: Editing the reply..."
$editReplyResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/replies/$replyId`?newContent=Updated%20Reply" -Method "PUT"
Write-TestResult -TestName "Edit Reply" -Success $editReplyResult.Success -Response $editReplyResult.Response

# Test 8: Get tickets by status
Write-Host "`nTest 8: Getting tickets by status..."
$statusResult = Invoke-TicketRequest -Uri "$BaseUrl/status/OPEN"
Write-TestResult -TestName "Get Tickets by Status" -Success $statusResult.Success -Response $statusResult.Response

# Test 9: Get tickets by assignee
Write-Host "`nTest 9: Getting tickets by assignee..."
$assigneeResult = Invoke-TicketRequest -Uri "$BaseUrl/assignee/agent1"
Write-TestResult -TestName "Get Tickets by Assignee" -Success $assigneeResult.Success -Response $assigneeResult.Response

# Test 10: Search tickets
Write-Host "`nTest 10: Searching tickets..."
$searchResult = Invoke-TicketRequest -Uri "$BaseUrl/search?query=Updated"
Write-TestResult -TestName "Search Tickets" -Success $searchResult.Success -Response $searchResult.Response

# Test 11: Get reply tree
Write-Host "`nTest 11: Getting reply tree..."
$replyTreeResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/replies"
Write-TestResult -TestName "Get Reply Tree" -Success $replyTreeResult.Success -Response $replyTreeResult.Response

# Test 12: Get ticket statistics
Write-Host "`nTest 12: Getting ticket statistics..."
$statsResult = Invoke-TicketRequest -Uri "$BaseUrl/statistics"
Write-TestResult -TestName "Get Statistics" -Success $statsResult.Success -Response $statsResult.Response

# Test 13: Get recent tickets
Write-Host "`nTest 13: Getting recent tickets..."
$recentResult = Invoke-TicketRequest -Uri "$BaseUrl/recent?limit=5"
Write-TestResult -TestName "Get Recent Tickets" -Success $recentResult.Success -Response $recentResult.Response

# Test 14: Get unassigned tickets
Write-Host "`nTest 14: Getting unassigned tickets..."
$unassignedResult = Invoke-TicketRequest -Uri "$BaseUrl/unassigned"
Write-TestResult -TestName "Get Unassigned Tickets" -Success $unassignedResult.Success -Response $unassignedResult.Response

# Test 15: Get overdue tickets
Write-Host "`nTest 15: Getting overdue tickets..."
$overdueResult = Invoke-TicketRequest -Uri "$BaseUrl/overdue"
Write-TestResult -TestName "Get Overdue Tickets" -Success $overdueResult.Success -Response $overdueResult.Response

# Test 16: Get tickets by department
Write-Host "`nTest 16: Getting tickets by department..."
$deptResult = Invoke-TicketRequest -Uri "$BaseUrl/departments"
Write-TestResult -TestName "Get Tickets by Department" -Success $deptResult.Success -Response $deptResult.Response

# Test 17: Resolve the ticket
Write-Host "`nTest 17: Resolving the ticket..."
$resolveResult = Invoke-TicketRequest -Uri "$BaseUrl/$ticketId/resolve" -Method "PUT"
Write-TestResult -TestName "Resolve Ticket" -Success $resolveResult.Success -Response $resolveResult.Response

# Print final summary
Write-Host "`nTest Summary:" -ForegroundColor $Blue
Write-Host "Tests Passed: $TestsPassed" -ForegroundColor $Green
Write-Host "Tests Failed: $TestsFailed" -ForegroundColor $Red
Write-Host "Total Tests: $($TestsPassed + $TestsFailed)" -ForegroundColor $White

# Exit with status code based on test results
if ($TestsFailed -eq 0) {
    exit 0
} else {
    exit 1
}