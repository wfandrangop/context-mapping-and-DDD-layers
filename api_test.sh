#!/bin/sh
BASE="http://localhost:8080"

echo "========================================"
echo "  CONTEXT 1: WORKER PROFILE"
echo "========================================"

echo "\n--- 1A: Register Maria Garcia (Plumber) ---"
MARIA=$(curl -s -X POST "$BASE/api/profiles" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Maria Garcia","phoneNumber":"+593991234567","experienceDescription":"10 years plumbing experience, certified in residential and commercial pipe installations","toolPhotoUrls":["https://example.com/tools/wrench.jpg"]}')
echo "$MARIA"

echo "\n--- 1B: Register Carlos Rodriguez (Electrician) ---"
CARLOS=$(curl -s -X POST "$BASE/api/profiles" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Carlos Rodriguez","phoneNumber":"+593997654321","experienceDescription":"8 years electrical work, panel upgrades and smart home installations","toolPhotoUrls":["https://example.com/tools/multimeter.jpg"]}')
echo "$CARLOS"

echo "\n--- 1C: Register Ana Lopez (General Maintenance) ---"
ANA=$(curl -s -X POST "$BASE/api/profiles" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ana Lopez","phoneNumber":"+593998887766","experienceDescription":"5 years painting, drywall repair, furniture assembly","toolPhotoUrls":[]}')
echo "$ANA"

echo "\n--- 1D: Register with EMPTY name (EXPECT ERROR) ---"
curl -s -X POST "$BASE/api/profiles" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"","phoneNumber":"+593991111111","experienceDescription":"test","toolPhotoUrls":[]}'

echo "\n--- 1E: Register DUPLICATE phone (EXPECT ERROR) ---"
curl -s -X POST "$BASE/api/profiles" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Juan Perez","phoneNumber":"+593991234567","experienceDescription":"duplicate test","toolPhotoUrls":[]}'

echo "\n\n========================================"
echo "  CONTEXT 2: SERVICE EXECUTION"
echo "========================================"

echo "\n--- 2A: Create Service Execution (client-001 hires worker-maria) ---"
EXEC1=$(curl -s -X POST "$BASE/api/service-executions" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-001","workerId":"worker-maria"}')
echo "$EXEC1"
EXEC1_ID=$(echo "$EXEC1" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Extracted ID: $EXEC1_ID"

echo "\n--- 2B: Get Execution by ID ---"
curl -s "$BASE/api/service-executions/$EXEC1_ID"

echo "\n--- 2C: Begin Execution ---"
curl -s -X PUT "$BASE/api/service-executions/$EXEC1_ID/begin"

echo "\n--- 2D: Try to complete WITHOUT photo (EXPECT 409 CONFLICT) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE/api/service-executions/$EXEC1_ID/complete" \
  -H "Content-Type: application/json" \
  -d '{"clientRating":5,"clientComment":"Great work!"}'

echo "\n--- 2E: Upload evidence photo ---"
echo "fake-image-content" > /tmp/evidence.jpg
curl -s -X POST "$BASE/api/service-executions/$EXEC1_ID/photos" \
  -F "file=@/tmp/evidence.jpg;type=image/jpeg"

echo "\n--- 2F: Complete Execution (5-star rating) ---"
curl -s -X PUT "$BASE/api/service-executions/$EXEC1_ID/complete" \
  -H "Content-Type: application/json" \
  -d '{"clientRating":5,"clientComment":"Excellent plumber, fixed the leak perfectly!"}'

echo "\n--- 2G: Create and Complete SECOND execution for worker-maria ---"
EXEC2=$(curl -s -X POST "$BASE/api/service-executions" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-002","workerId":"worker-maria"}')
echo "$EXEC2"
EXEC2_ID=$(echo "$EXEC2" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
curl -s -X PUT "$BASE/api/service-executions/$EXEC2_ID/begin" > /dev/null
curl -s -X POST "$BASE/api/service-executions/$EXEC2_ID/photos" -F "file=@/tmp/evidence.jpg;type=image/jpeg" > /dev/null
curl -s -X PUT "$BASE/api/service-executions/$EXEC2_ID/complete" \
  -H "Content-Type: application/json" \
  -d '{"clientRating":4,"clientComment":"Good job, slightly delayed but quality work."}'

echo "\n--- 2H: Create and Complete execution for worker-carlos ---"
EXEC3=$(curl -s -X POST "$BASE/api/service-executions" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-003","workerId":"worker-carlos"}')
EXEC3_ID=$(echo "$EXEC3" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
curl -s -X PUT "$BASE/api/service-executions/$EXEC3_ID/begin" > /dev/null
curl -s -X POST "$BASE/api/service-executions/$EXEC3_ID/photos" -F "file=@/tmp/evidence.jpg;type=image/jpeg" > /dev/null
curl -s -X PUT "$BASE/api/service-executions/$EXEC3_ID/complete" \
  -H "Content-Type: application/json" \
  -d '{"clientRating":3,"clientComment":"Decent electrical work but communication could improve."}'

echo "\n--- 2I: Try completing an already FINALIZED execution (EXPECT ERROR) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE/api/service-executions/$EXEC1_ID/complete" \
  -H "Content-Type: application/json" \
  -d '{"clientRating":5,"clientComment":"Again?"}'

echo "\n\n========================================"
echo "  CONTEXT 3: REPUTATION (Downstream from ServiceExecution)"
echo "========================================"

echo "\n--- 3A: Get Reputation for worker-maria (2 completed jobs) ---"
curl -s "$BASE/api/reputations/worker-maria"

echo "\n--- 3B: Get Reputation for worker-carlos (1 completed job) ---"
curl -s "$BASE/api/reputations/worker-carlos"

echo "\n--- 3C: Get Reputation for NON-EXISTENT worker (EXPECT 404) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE/api/reputations/worker-nobody"

echo "\n\n========================================"
echo "  CONTEXT 4: JOB MARKETPLACE"
echo "========================================"

echo "\n--- 4A: Publish a plumbing demand ---"
JOB1=$(curl -s -X POST "$BASE/api/job-posts" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-001","technicalRequirements":["plumbing","pipe-fitting"],"minimumBudget":50.00,"maximumBudget":200.00,"urgency":"IMMEDIATE"}')
echo "$JOB1"
JOB1_ID=$(echo "$JOB1" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Job Post ID: $JOB1_ID"

echo "\n--- 4B: Publish an electrical demand ---"
JOB2=$(curl -s -X POST "$BASE/api/job-posts" \
  -H "Content-Type: application/json" \
  -d '{"clientId":"client-002","technicalRequirements":["electrical","wiring"],"minimumBudget":100.00,"maximumBudget":500.00,"urgency":"MEDIUM"}')
echo "$JOB2"
JOB2_ID=$(echo "$JOB2" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)

echo "\n--- 4C: List all OPEN demands ---"
curl -s "$BASE/api/job-posts/open"

echo "\n--- 4D: worker-maria applies to plumbing job ---"
curl -s -X POST "$BASE/api/job-posts/$JOB1_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-maria"}'

echo "\n--- 4E: worker-carlos applies to plumbing job ---"
curl -s -X POST "$BASE/api/job-posts/$JOB1_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-carlos"}'

echo "\n--- 4F: Get job post details (should show 2 applicants) ---"
curl -s "$BASE/api/job-posts/$JOB1_ID"

echo "\n--- 4G: DUPLICATE application (EXPECT ERROR) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE/api/job-posts/$JOB1_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-maria"}'

echo "\n--- 4H: Select worker-maria for the plumbing job ---"
curl -s -X POST "$BASE/api/job-posts/$JOB1_ID/select" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-maria"}'

echo "\n--- 4I: Try applying to a CLOSED demand (EXPECT ERROR) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE/api/job-posts/$JOB1_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-ana"}'

echo "\n--- 4J: Auto-select best candidate for electrical job ---"
curl -s -X POST "$BASE/api/job-posts/$JOB2_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-maria"}' > /dev/null
curl -s -X POST "$BASE/api/job-posts/$JOB2_ID/applications" \
  -H "Content-Type: application/json" \
  -d '{"workerProfileId":"worker-carlos"}' > /dev/null
echo "Auto-selecting best candidate (empty body = use ReputationProvider):"
curl -s -X POST "$BASE/api/job-posts/$JOB2_ID/select" \
  -H "Content-Type: application/json" \
  -d '{}'

echo "\n--- 4K: List open demands (should be empty now) ---"
curl -s "$BASE/api/job-posts/open"

echo "\n--- 4L: Get non-existent job post (EXPECT ERROR) ---"
curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE/api/job-posts/00000000-0000-0000-0000-000000000000"

echo "\n\n========================================"
echo "  CROSS-CONTEXT: VERIFY DB STATE"
echo "========================================"

echo "\nDone. Check PostgreSQL tables for final state."
