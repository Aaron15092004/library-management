-- Insert sample categories
IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Văn học')
    INSERT INTO Category (CategoryName) VALUES (N'Văn học');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Khoa học')
    INSERT INTO Category (CategoryName) VALUES (N'Khoa học');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Lịch sử')
    INSERT INTO Category (CategoryName) VALUES (N'Lịch sử');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Công nghệ')
    INSERT INTO Category (CategoryName) VALUES (N'Công nghệ');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Kinh tế')
    INSERT INTO Category (CategoryName) VALUES (N'Kinh tế');

-- Insert sample publishers
IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Kim Đồng')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Kim Đồng');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Trẻ')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Trẻ');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Giáo dục')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Giáo dục');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Lao động')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Lao động');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Văn học')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Văn học');

-- Insert sample roles
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Admin')
    INSERT INTO Role (RoleName) VALUES (N'Admin');

IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Staff')
    INSERT INTO Role (RoleName) VALUES (N'Staff');

IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Librarian')
    INSERT INTO Role (RoleName) VALUES (N'Librarian');
