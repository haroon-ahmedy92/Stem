# Homepage API Testing Guide

## 🧪 **API Endpoint Testing Instructions**

### Prerequisites
1. Start the Spring Boot application: `mvn spring-boot:run`
2. Ensure the application is running on `http://localhost:8080`
3. Have an admin JWT token ready for protected endpoints

---

## 📋 **Test Cases for All 17 Endpoints**

### **🔓 PUBLIC ENDPOINTS (No Authentication Required)**

#### 1. GET Complete Homepage Content
```bash
curl -X GET "http://localhost:8080/api/homepage-content" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with complete homepage data (hero, activities, outcomes, sections)

#### 2. GET Hero Section
```bash
curl -X GET "http://localhost:8080/api/homepage-content/hero" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with hero section data

#### 3. GET Activities List
```bash
curl -X GET "http://localhost:8080/api/homepage-content/activities" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with activities array

#### 4. GET Outcomes List
```bash
curl -X GET "http://localhost:8080/api/homepage-content/outcomes" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with outcomes array

#### 5. GET Section by Type
```bash
curl -X GET "http://localhost:8080/api/homepage-content/sections/ACTIVITIES" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with section data

#### 6. GET Search Content
```bash
curl -X GET "http://localhost:8080/api/homepage-content/search?q=STEM" \
  -H "Content-Type: application/json"
```
**Expected:** 200 OK with search results

---

### **🔒 ADMIN PROTECTED ENDPOINTS (Require Authentication)**

#### 7. GET Analytics (Admin Only)
```bash
curl -X GET "http://localhost:8080/api/homepage-content/analytics" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```
**Expected:** 200 OK with analytics data

#### 8. UPDATE Hero Section (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/hero" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "Updated STEM Education Title",
    "subtitle": "Updated subtitle for testing",
    "ctaText": "Updated CTA",
    "ctaLink": "/updated-link"
  }'
```
**Expected:** 200 OK with updated hero data

#### 9. CREATE Activity (Admin Only)
```bash
curl -X POST "http://localhost:8080/api/homepage-content/activities" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "New Test Activity",
    "description": "This is a test activity created via API",
    "iconClass": "fas fa-test",
    "color": "#ff5722"
  }'
```
**Expected:** 201 Created with new activity data

#### 10. UPDATE Activity (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/activities/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "Updated Activity Title",
    "description": "Updated description"
  }'
```
**Expected:** 200 OK with updated activity data

#### 11. DELETE Activity (Admin Only)
```bash
curl -X DELETE "http://localhost:8080/api/homepage-content/activities/1" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```
**Expected:** 200 OK with success message

#### 12. REORDER Activities (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/activities/reorder" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "itemIds": [3, 1, 2, 4, 5, 6]
  }'
```
**Expected:** 200 OK with success message

#### 13. CREATE Outcome (Admin Only)
```bash
curl -X POST "http://localhost:8080/api/homepage-content/outcomes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "New Test Outcome",
    "description": "This is a test outcome created via API",
    "iconClass": "fas fa-chart-bar"
  }'
```
**Expected:** 201 Created with new outcome data

#### 14. UPDATE Outcome (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/outcomes/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "Updated Outcome Title",
    "description": "Updated description"
  }'
```
**Expected:** 200 OK with updated outcome data

#### 15. DELETE Outcome (Admin Only)
```bash
curl -X DELETE "http://localhost:8080/api/homepage-content/outcomes/1" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```
**Expected:** 200 OK with success message

#### 16. REORDER Outcomes (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/outcomes/reorder" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "itemIds": [2, 1, 3, 4, 5, 6]
  }'
```
**Expected:** 200 OK with success message

#### 17. UPDATE Section (Admin Only)
```bash
curl -X PUT "http://localhost:8080/api/homepage-content/sections/ACTIVITIES" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -d '{
    "title": "Updated Activities Section",
    "content": "Updated content for activities section",
    "backgroundColor": "#f0f0f0"
  }'
```
**Expected:** 200 OK with updated section data

---

## 🛡️ **Security Testing**

### Test Unauthorized Access
Try accessing admin endpoints without authorization:
```bash
curl -X POST "http://localhost:8080/api/homepage-content/activities" \
  -H "Content-Type: application/json" \
  -d '{"title": "Unauthorized Test"}'
```
**Expected:** 401 Unauthorized or 403 Forbidden

### Test Invalid JWT Token
```bash
curl -X POST "http://localhost:8080/api/homepage-content/activities" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer INVALID_TOKEN" \
  -d '{"title": "Invalid Token Test"}'
```
**Expected:** 401 Unauthorized

---

## 📊 **Response Format Validation**

### Verify JSON Response Structure
All responses should follow this format:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2025-07-05T10:30:00Z"
}
```

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "error": "Error details",
  "timestamp": "2025-07-05T10:30:00Z"
}
```

---

## 🔍 **Testing Checklist**

- [ ] All 5 public GET endpoints return 200 OK
- [ ] All 12 admin endpoints require authentication
- [ ] CRUD operations work correctly for activities
- [ ] CRUD operations work correctly for outcomes
- [ ] Reordering functionality works for both activities and outcomes
- [ ] Search functionality returns relevant results
- [ ] Analytics endpoint returns proper metrics
- [ ] All responses follow correct JSON format
- [ ] Error handling works for invalid requests
- [ ] Security is properly enforced

---

## 🚀 **Ready for Production**

Once all tests pass:
1. ✅ All 17 endpoints are functional
2. ✅ Security is properly implemented
3. ✅ Data validation works correctly
4. ✅ Error handling is comprehensive
5. ✅ JSON responses are properly formatted

**The Homepage API is production-ready!**
