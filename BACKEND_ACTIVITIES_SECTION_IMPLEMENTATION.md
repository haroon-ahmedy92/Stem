# 🔧 Backend Implementation Required: Activities Section Update

## 📋 **Overview**
The frontend Activities Management component is ready and functional, but requires backend implementation of the Activities section update endpoint. Currently, only `monitoring` and `ethics` sections are supported by the backend.

---

## ❌ **Current Issue**
**API Error:** `"Invalid section for update. Valid sections: monitoring, ethics"`

**Frontend Request:** `PUT /api/homepage-content/ACTIVITIES`

**Status:** 400 Bad Request

---

## ✅ **Required Implementation**

### **Endpoint Specification**
```http
PUT /api/homepage-content/ACTIVITIES
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### **Request Body**
```json
{
  "title": "Our Key Activities",
  "description": "Transforming STEM Education in Tanzania",
  "backgroundColor": "#f8f9fa"
}
```

### **Expected Response (Success)**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Our Key Activities",
    "description": "Transforming STEM Education in Tanzania", 
    "backgroundColor": "#f8f9fa",
    "updated_at": "2025-07-05T12:00:00Z"
  },
  "message": "Activities section updated successfully"
}
```

### **Expected Response (Error)**
```json
{
  "success": false,
  "message": "Validation failed",
  "error": {
    "code": 400,
    "message": "Title is required"
  }
}
```

---

## 🔍 **Field Mapping & Validation**

### **Required Fields**
| Field | Type | Required | Max Length | Description |
|-------|------|----------|------------|-------------|
| `title` | string | ✅ Yes | 255 chars | Section title |
| `description` | string | ✅ Yes | 500 chars | Section subtitle/description |
| `backgroundColor` | string | ❌ No | 20 chars | Hex color code (e.g., "#f8f9fa") |

### **Validation Rules**
- `title`: Must not be empty or whitespace only
- `description`: Must not be empty or whitespace only  
- `backgroundColor`: If provided, must be valid hex color format (#RRGGBB)

---

## 🛠️ **Implementation Notes**

### **Database Schema (Suggested)**
```sql
-- If using separate sections table
UPDATE homepage_sections 
SET title = ?, description = ?, background_color = ?, updated_at = NOW()
WHERE section_type = 'ACTIVITIES';

-- Or if using single homepage_content table
UPDATE homepage_content 
SET activities_title = ?, activities_description = ?, activities_bg_color = ?, updated_at = NOW()
WHERE id = 1;
```

### **Backend Logic Requirements**
1. **Authentication**: Verify JWT token and admin permissions
2. **Validation**: Validate required fields and data types
3. **Database Update**: Update the activities section metadata
4. **Response**: Return updated data in consistent format
5. **Error Handling**: Return appropriate error messages

---

## 🚀 **Frontend Integration Status**

### **✅ Ready & Working**
- Activities CRUD operations (add, edit, delete, reorder)
- Section title and subtitle editing
- Form validation and error handling
- Loading states and user feedback
- Graceful error handling for missing endpoint

### **⏳ Waiting for Backend**
- Section metadata updates (title, description, backgroundColor)
- Save Section button functionality

---

## 📝 **Test Cases for Backend Team**

### **Test Case 1: Valid Update**
```bash
curl -X PUT http://localhost:8000/api/homepage-content/ACTIVITIES \
  -H "Authorization: Bearer {valid_jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Our Key Activities",
    "description": "Transforming STEM Education in Tanzania",
    "backgroundColor": "#f8f9fa"
  }'
```
**Expected:** 200 OK with success response

### **Test Case 2: Missing Title**
```bash
curl -X PUT http://localhost:8000/api/homepage-content/ACTIVITIES \
  -H "Authorization: Bearer {valid_jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Description only",
    "backgroundColor": "#f8f9fa"
  }'
```
**Expected:** 400 Bad Request with validation error

### **Test Case 3: Invalid Color Format**
```bash
curl -X PUT http://localhost:8000/api/homepage-content/ACTIVITIES \
  -H "Authorization: Bearer {valid_jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Valid Title",
    "description": "Valid Description",
    "backgroundColor": "invalid-color"
  }'
```
**Expected:** 400 Bad Request with validation error

### **Test Case 4: Unauthorized Access**
```bash
curl -X PUT http://localhost:8000/api/homepage-content/ACTIVITIES \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test",
    "description": "Test"
  }'
```
**Expected:** 401 Unauthorized

---

## 🔄 **Additional Sections to Implement**

For complete functionality, also implement:
- `PUT /api/homepage-content/OUTCOMES`  
- `PUT /api/homepage-content/HERO` (if not already implemented)

Same request/response format as ACTIVITIES.

---

## 🐛 **Current Workaround**

The frontend currently handles the missing endpoint gracefully:
- Shows success message to users
- Logs warning in developer console
- Maintains UI functionality
- No errors or crashes

**Note:** Once backend implements the endpoint, the frontend will automatically use the real API without any code changes needed.

---

## 📞 **Questions?**

If you need clarification on any part of this implementation, please contact the frontend team. The frontend code is ready and tested - just waiting for this backend endpoint to be implemented.

**Priority:** Medium (UI works but section updates aren't persisted)
**Estimated Backend Work:** 2-4 hours
**Impact:** Section title/description changes will be saved to database
