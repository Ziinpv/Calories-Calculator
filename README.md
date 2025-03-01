# 📌 PHẦN 1: LÀM VIỆC VỚI GITHUB TRONG ANDROID STUDIO

## 1️⃣ Cách Clone Repository từ GitHub

### 📌 Clone bằng giao diện Android Studio
1. Vào **File** → **New** → **Project from Version Control**.
2. Chọn **Git**, nhập URL GitHub repo.
3. Chọn thư mục lưu project và nhấn **Clone**.

### 📌 Clone bằng Git Bash
```sh
git clone <URL_repo>
```

---

## 2️⃣ Cách Push Code Lên GitHub

### 📌 Push bằng giao diện Android Studio
1. Chỉnh sửa code, vào **VCS** → **Commit** (`Ctrl + K`).
2. Viết mô tả commit → Chọn **Commit and Push** (`Ctrl + Shift + K`).
3. Chọn nhánh và **Push** lên GitHub.

### 📌 Push bằng Git Bash
```sh
git add .
git commit -m "Mô tả commit"
git push origin main
```

---

## 3️⃣ Cách Pull Code Từ GitHub

### 📌 Pull bằng giao diện Android Studio
1. Vào **VCS** → **Git** → **Pull**.
2. Chọn nhánh cần pull và nhấn **OK**.

### 📌 Pull bằng Git Bash
```sh
git pull origin main
```

---

## 4️⃣ Xử Lý Xung Đột (Conflict)

### 📌 Khi xảy ra xung đột
- Nếu có xung đột khi pull, Android Studio sẽ cảnh báo.
- Mở **VCS** → **Git** → **Resolve Conflicts** để xem file bị xung đột.
- Chỉnh sửa file thủ công, chọn phiên bản phù hợp.
- Chọn **Mark as Resolved**, sau đó commit lại code.

### 📌 Xử lý xung đột bằng Git Bash
```sh
git status  # Kiểm tra file bị xung đột
git add <file_conflict>  # Đánh dấu file đã sửa
git commit -m "Fix conflict"
git push origin main
```

---

# 📌 PHẦN 2: TEST MODE FIRESTORE

## 1️⃣ Test Mode Là Gì?
- **Test Mode**: Cho phép mọi người truy cập và chỉnh sửa dữ liệu trong **30 ngày**.
- Sau 30 ngày, bạn phải cập nhật lại **Security Rules** để bảo vệ dữ liệu.

## 2️⃣ Cách Cấu Hình Test Mode Trong Firestore
### 🔹 Khi tạo Firestore Database:
1. Chọn **Start in test mode**.
2. Nhấn **Create**.

### 🔹 Mặc định, Test Mode có Security Rules như sau:
```javascript
rules_version = '2';
service cloud.firestore {
    match /databases/{database}/documents {
        match /{document=**} {
            allow read, write: if request.time < timestamp.date(2025, 4, 1);
        }
    }
}
```

## 3️⃣ Cách Chuyển Firestore Sang Chế Độ Bảo Mật (Production Mode)
### 🔹 Cập nhật Security Rules trong **Firebase Console**:
1. Vào **Firestore Database** → **Rules**.
2. Thay Security Rules thành:
```javascript
rules_version = '2';
service cloud.firestore {
    match /databases/{database}/documents {
        match /{document=**} {
            allow read, write: if request.auth != null;
        }
    }
}
```
3. Nhấn **Publish** để lưu lại.

---
✅ **Tổng kết:** README này giúp bạn làm việc với **GitHub** trong Android Studio và cấu hình **Test Mode Firestore** một cách an toàn. 🚀

