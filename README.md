# Group_Assignment_Team_11
## 1. Project Title
**Digital University Management System**  
A Java Swing–based application for managing university operations, including student, faculty, registrar, and administrator workflows.

---

## 2. Team Information

| Name | NUID | Role | Responsibilities |
|------|------|------|------------------|
| Yen-Chia Chiu | 002507522 | Administrator UI Developer | Designed and implemented the Admin Dashboard (user management, registrar records, analytics, student/faculty management, profile) |
| Yudan Zhou | 002596888 | Faculty Module Developer | Implemented faculty features (assignment creation, grading, performance reports) |
| Metreethumaporn, Chotipat |Student Module Developer / Repository Maintainer | Developed student use cases (course enrollment, tuition payment, GPA view) and managed GitHub repository integration |
| Jiayu Zhu | 002875077 | 002597192 | Registrar Module Developer | Managed registrar workflows (course creation, enrollment, report generation) |

---

## 3. Project Overview

### Purpose
This project simulates a digitalized university management system that centralizes data for students, faculty, registrars, and administrators.

### Objectives
- Simplify university record management.
- Support CRUD operations for all roles.
- Implement authentication and authorization to enforce role-based access.
- Provide analytics and visualization for administrative insights.

### Key Features
- Multi-role login system (Admin, Faculty, Student, Registrar)
- Role-specific dashboards and operations
- Centralized data management via UniversityDirectory
- Real-time reporting and analytics
- Profile editing and account security

---
## 4. Installation & Setup Instructions

### 4.1 Prerequisites
- **Operating System:** Windows, macOS, or Linux
- **Java Development Kit (JDK):** 21 (or 17+ if your IDE is configured for it)
- **IDE:** NetBeans 19+ (recommended) or IntelliJ IDEA / Eclipse
- **Build Tools:** (Optional) Maven 3.9+ or Gradle 8+ if you plan to import as a Maven/Gradle project
- **Git:** 2.30+ (for cloning the repository)
- **Swing:** Included with the JDK; no extra installation required

> Notes  
> 1) If your NetBeans default JDK is older than the project’s `source` level, update the project’s Java Platform to JDK 21 under Project Properties.  
> 2) If you see “system modules path not set in conjunction with -source XX”, make sure the project’s Java Platform points to a full JDK (not a JRE) and the **Source/Binary Format** matches your JDK version.

### 4.2 Clone the Repository
```bash
# Choose a local folder and clone
git clone https://github.com/Barry0777/Group_Assignment_Team_11.git
cd Group_Assignment_Team_11/DigitalUniversity

---

## 5. Authentication & Access Control
### 5.1 Authentication Process
1. Users access the login interface at program start.
2. They enter a **username** and **password**.
3. The system verifies credentials via the `AuthenticationService` class, which checks against the stored user accounts in memory.
4. If successful, the system loads the appropriate dashboard according to the user’s role (Admin, Faculty, Student, or Registrar).
5. If authentication fails, an error message is displayed and access is denied.

### 5.2 Authorization Rules

| Role | Access Rights |
|------|----------------|
| **Admin** | Full control of the system. Can create, edit, and delete user accounts, view analytics, and manage all records. |
| **Faculty** | Limited to managing courses, grading students, and viewing performance reports. |
| **Student** | Can enroll in courses, view grades, pay tuition, and update personal profile. |
| **Registrar** | Handles course creation, assigns faculty, and manages enrollment records. |

---

## 6. Features Implemented

| Role | Key Functionalities | Developer |
|------|----------------------|------------|
| **Admin** | User Account Management, Registrar Records Management, Student/Faculty Records Management, Analytics Dashboard, Profile Management | Yen-Chia Chiu |
| **Faculty** | Course creation, Assignment grading, Student evaluation reports | Yudan Zhou |
| **Student** | Course registration, Grade viewing, Tuition payment, Profile editing | Barry Lin |
| **Registrar** | Course scheduling, Faculty assignment, Enrollment report generation | Metree Su |

---

## 7. Usage Instructions

### 7.1 Launching the Application
1. Open the project in NetBeans and run `MainJFrame.java`.
2. On the login screen, use one of the default test accounts:

| Username | Password | Role |
|-----------|-----------|------|
| admin | admin123 | Admin |
| faculty1 | pass1 | Faculty |
| student1 | pass1 | Student |
| registrar01 | 1234 | Registrar |

### 7.2 Example Scenarios

#### Admin Workflow
- Log in as `admin`.
- Add new user accounts through **User Account Management**.
- View or delete existing users.
- Check overall statistics via **Analytics Dashboard**.

#### Faculty Workflow
- Log in as `faculty01`.
- View courses assigned.
- Input grades for enrolled students.
- Review performance summaries.

#### Student Workflow
- Log in as `student01`.
- Register for courses offered.
- View grades and GPA.
- Make tuition payments if applicable.

#### Registrar Workflow
- Log in as `registrar01`.
- Create or modify course offerings.
- Assign faculty members to courses.
- Export enrollment and performance data.

---

## 8. Testing Guide

### 8.1 Manual Testing

| Test Case | Steps | Expected Result |
|------------|-------|------------------|
| Login Validation | Enter valid/invalid credentials | Grants or denies access |
| Create User (Admin) | Add a new user account | New user appears in table |
| Edit User (Admin) | Modify username or email | Updated record visible immediately |
| Search Records (Admin) | Enter student/faculty name or ID | Returns matching entries |
| Faculty Grade Input | Faculty updates student grades | Stored and reflected in transcript |
| Student Tuition Payment | Simulate payment submission | Updates student balance |
| Registrar Course Management | Create and assign course | Appears under correct semester |

### 8.2 Authentication & Authorization Testing
- Verify that each role only sees its own dashboard and functions.
- Attempt to perform unauthorized actions and confirm the system restricts them.
- Test login with incorrect passwords to confirm proper error handling.

---

## 9. Challenges & Solutions

| Challenge | Solution |
|------------|-----------|
| Maintaining consistent data between roles | Implemented centralized data management through `UniversityDirectory`. |
| Handling null `Person` references during user creation | Added automatic temporary `Person` instantiation in `AdminDashboard`. |
| Synchronizing UI updates in Swing tables | Used `DefaultTableModel` and event-driven refresh (`fireTableDataChanged()`). |
| Modularizing role-based access | Separated UI logic per role (`AdminDashboard`, `FacultyDashboard`, etc.). |
| Avoiding hardcoded data dependencies | Built service layers (`AdminService`, `ReportService`, etc.) to encapsulate logic. |

---

## 10. Future Enhancements

- Integrate persistent storage (e.g., MySQL or SQLite database).
- Implement password encryption (hashing and salting).
- Add graphical data visualization in analytics dashboards.
- Allow exporting reports in PDF or Excel format.
- Introduce role-based notifications or messaging between users.
- Add input validation and exception handling for all user fields.

---

## 11. Contribution Breakdown

| Team Member | Coding | Documentation | Testing | UI Design | Repository Management |
|--------------|---------|---------------|----------|------------|------------------------|
| **Yen-Chia Chiu** | Admin Dashboard implementation, Service integration | Authored README and in-code documentation | Performed functional testing | Designed Admin UI | Managed commits and merges |
| **Yudan Zhou** | Faculty Use Case and grading logic | Assisted documentation | Unit testing | UI improvement |  |
| **Metreethumaporn, Chotipat**  | Student Use Case and tuition system | Project structure and code review | Test scenario creation | GUI polish | GitHub repository setup |
| **Jiayu Zhu** | Registrar workflows and enrollment logic | Contributed documentation | Data validation tests |  |  |

---

**Repository Link:**  

