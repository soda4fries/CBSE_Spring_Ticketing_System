#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color
BLUE='\033[0;34m'

# Base URL
BASE_URL="http://localhost:8080/api/tickets"

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Function to print test results
print_test_result() {
    local test_name=$1
    local result=$2
    local response=$3
    
    if [ $result -eq 0 ]; then
        echo -e "${GREEN}✓ $test_name passed${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        echo -e "${RED}✗ $test_name failed${NC}"
        echo -e "${RED}Response: $response${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

echo -e "${BLUE}Starting Ticket System API Tests...${NC}\n"

# Test 1: Create a ticket
echo "Test 1: Creating a ticket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL?title=Test%20Ticket&description=Test%20Description")
TICKET_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
print_test_result "Create Ticket" $? "$CREATE_RESPONSE"

# Test 2: Get the created ticket
echo -e "\nTest 2: Getting the ticket..."
GET_RESPONSE=$(curl -s -X GET "$BASE_URL/$TICKET_ID")
print_test_result "Get Ticket" $? "$GET_RESPONSE"

# Test 3: Update the ticket
echo -e "\nTest 3: Updating the ticket..."
UPDATE_RESPONSE=$(curl -s -X PUT -H "Content-Type: application/json" \
    -d "{\"id\":\"$TICKET_ID\",\"title\":\"Updated Ticket\",\"description\":\"Updated Description\"}" \
    "$BASE_URL/$TICKET_ID")
print_test_result "Update Ticket" $? "$UPDATE_RESPONSE"

# Test 4: Assign the ticket
echo -e "\nTest 4: Assigning the ticket..."
ASSIGN_RESPONSE=$(curl -s -X PUT "$BASE_URL/$TICKET_ID/assign?userId=agent1")
print_test_result "Assign Ticket" $? "$ASSIGN_RESPONSE"

# Test 5: Add a reply
echo -e "\nTest 5: Adding a reply..."
REPLY_RESPONSE=$(curl -s -X POST "$BASE_URL/$TICKET_ID/replies?content=Test%20Reply")
REPLY_ID=$(echo $REPLY_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
print_test_result "Add Reply" $? "$REPLY_RESPONSE"

# Test 6: Add a nested reply
echo -e "\nTest 6: Adding a nested reply..."
NESTED_REPLY_RESPONSE=$(curl -s -X POST "$BASE_URL/$TICKET_ID/replies?content=Nested%20Reply&parentReplyId=$REPLY_ID")
print_test_result "Add Nested Reply" $? "$NESTED_REPLY_RESPONSE"

# Test 7: Edit reply
echo -e "\nTest 7: Editing the reply..."
EDIT_REPLY_RESPONSE=$(curl -s -X PUT "$BASE_URL/$TICKET_ID/replies/$REPLY_ID?newContent=Updated%20Reply")
print_test_result "Edit Reply" $? "$EDIT_REPLY_RESPONSE"

# Test 8: Get tickets by status
echo -e "\nTest 8: Getting tickets by status..."
STATUS_RESPONSE=$(curl -s -X GET "$BASE_URL/status/OPEN")
print_test_result "Get Tickets by Status" $? "$STATUS_RESPONSE"

# Test 9: Get tickets by assignee
echo -e "\nTest 9: Getting tickets by assignee..."
ASSIGNEE_RESPONSE=$(curl -s -X GET "$BASE_URL/assignee/agent1")
print_test_result "Get Tickets by Assignee" $? "$ASSIGNEE_RESPONSE"

# Test 10: Search tickets
echo -e "\nTest 10: Searching tickets..."
SEARCH_RESPONSE=$(curl -s -X GET "$BASE_URL/search?query=Updated")
print_test_result "Search Tickets" $? "$SEARCH_RESPONSE"

# Test 11: Get reply tree
echo -e "\nTest 11: Getting reply tree..."
REPLY_TREE_RESPONSE=$(curl -s -X GET "$BASE_URL/$TICKET_ID/replies")
print_test_result "Get Reply Tree" $? "$REPLY_TREE_RESPONSE"

# Test 12: Get ticket statistics
echo -e "\nTest 12: Getting ticket statistics..."
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics")
print_test_result "Get Statistics" $? "$STATS_RESPONSE"

# Test 13: Get recent tickets
echo -e "\nTest 13: Getting recent tickets..."
RECENT_RESPONSE=$(curl -s -X GET "$BASE_URL/recent?limit=5")
print_test_result "Get Recent Tickets" $? "$RECENT_RESPONSE"

# Test 14: Get unassigned tickets
echo -e "\nTest 14: Getting unassigned tickets..."
UNASSIGNED_RESPONSE=$(curl -s -X GET "$BASE_URL/unassigned")
print_test_result "Get Unassigned Tickets" $? "$UNASSIGNED_RESPONSE"

# Test 15: Get overdue tickets
echo -e "\nTest 15: Getting overdue tickets..."
OVERDUE_RESPONSE=$(curl -s -X GET "$BASE_URL/overdue")
print_test_result "Get Overdue Tickets" $? "$OVERDUE_RESPONSE"

# Test 16: Get tickets by department
echo -e "\nTest 16: Getting tickets by department..."
DEPT_RESPONSE=$(curl -s -X GET "$BASE_URL/departments")
print_test_result "Get Tickets by Department" $? "$DEPT_RESPONSE"

# Test 17: Resolve the ticket
echo -e "\nTest 17: Resolving the ticket..."
RESOLVE_RESPONSE=$(curl -s -X PUT "$BASE_URL/$TICKET_ID/resolve")
print_test_result "Resolve Ticket" $? "$RESOLVE_RESPONSE"

# Print final summary
echo -e "\n${BLUE}Test Summary:${NC}"
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo -e "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"

# Exit with status code based on test results
if [ $TESTS_FAILED -eq 0 ]; then
    exit 0
else
    exit 1
fi