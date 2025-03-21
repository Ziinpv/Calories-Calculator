# Hướng Dẫn Sử Dụng GitHub Trên Android Studio

## 🚀 1. Cài Đặt Git Và Đăng Nhập GitHub

### 🔹 Cài đặt Git trên máy tính
- Tải và cài đặt Git từ: [Git SCM](https://git-scm.com/downloads)
- Kiểm tra Git đã cài đặt bằng lệnh:
  ```sh
  git --version
  ```

### 🔹 Đăng nhập GitHub trong Android Studio
1. Mở **Android Studio** → **File** → **Settings** (hoặc **Preferences** trên macOS).
2. Chọn **Version Control** → **GitHub**.
3. Click **Add Account** → Chọn **Log in via GitHub** → Nhập thông tin tài khoản GitHub.

---

## 🌱 2. Tạo Hoặc Clone Repository

### 📌 Tạo một repository mới từ Android Studio
1. Vào **VCS** → **Import into Version Control** → **Share Project on GitHub**.
2. Điền thông tin:
   - **Repository name**: Tên repo trên GitHub.
   - **Description**: Mô tả dự án.
   - **Private/Public**: Chọn chế độ riêng tư hoặc công khai.
3. Click **Share** để đẩy code lên GitHub.

### 📌 Clone một repository từ GitHub
1. Vào **File** → **New** → **Project from Version Control**.
2. Chọn **Git** → Nhập URL GitHub repo.
3. Chọn thư mục lưu project và nhấn **Clone**.

---

## 🔥 3. Làm Việc Với Git Trong Android Studio

### ✅ Commit & Push code lên GitHub
1. Chỉnh sửa code xong, vào **VCS** → **Commit** (hoặc `Ctrl + K`).
2. Viết mô tả commit → Chọn **Commit** hoặc **Commit and Push**.
3. Nếu commit trước, vào **VCS** → **Git** → **Push** (`Ctrl + Shift + K`) để đẩy code.

### 🔄 Pull code mới từ GitHub
1. Vào **VCS** → **Git** → **Pull** để cập nhật code mới nhất.

### 🌿 Tạo nhánh mới (Branch)
1. Chọn **Git** trên thanh trạng thái Android Studio.
2. Chọn **New Branch** → Nhập tên nhánh mới → **Create**.
3. Chuyển nhánh bằng cách click vào tên nhánh.

### 🔄 Merge hoặc Rebase nhánh
1. Vào **VCS** → **Git** → **Branches**.
2. Chọn **Merge into Current** để gộp nhánh.

---

## ⚠️ 4. Xử Lý Xung Đột (Conflict)
- Khi có xung đột, Android Studio sẽ cảnh báo.
- Vào **VCS** → **Git** → **Resolve Conflicts** để chỉnh sửa file.
- Chọn phiên bản phù hợp hoặc chỉnh sửa thủ công.
- Chọn **Mark as Resolved**, rồi Commit lại code.

---

## 🔧 5. Làm Việc Với Git Bằng Terminal
Mở Terminal trong Android Studio (`Alt + F12`) và sử dụng:
```sh
# Kiểm tra trạng thái repo
git status

# Thêm toàn bộ file vào staging
git add .

# Commit thay đổi
git commit -m "Mô tả commit"

# Đẩy code lên GitHub
git push origin main

# Cập nhật code mới nhất
git pull origin main
```

---
# Phân Công Công Việc 

### Thành viên nhóm:

| Mã số sinh viên | Email                           | Tên                          | Phân công công việc                               |
|-----------------|---------------------------------|------------------------------|--------------------------------------------------|
| 2212451         | 2212451@dlu.edu.vn              | Nguyễn Hoàng Sang            | Sử lý sự kiện, DB                               |
| 2212407         | 2212407@dlu.edu.vn              | Hoàng Long                   | Sử lý sự kiện, DB, API                         |
| 2213795         | 2213795@dlu.edu.vn              | Nguyễn Thị Hoàng Phúc        | FontEnd                                          |

---

✨ **Chúc bạn thành công với GitHub trong Android Studio!** 🚀

