-- Ensure legacy schema is upgraded (some DBs were created without ReturnDate)
IF OBJECT_ID(N'Borrow', N'U') IS NOT NULL AND COL_LENGTH('Borrow', 'ReturnDate') IS NULL ALTER TABLE Borrow ADD ReturnDate DATE NULL
-- Insert sample categories
IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'VÄƒn há»c')
    INSERT INTO Category (CategoryName) VALUES (N'VÄƒn há»c');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Khoa há»c')
    INSERT INTO Category (CategoryName) VALUES (N'Khoa há»c');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Lá»‹ch sá»­')
    INSERT INTO Category (CategoryName) VALUES (N'Lá»‹ch sá»­');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'CÃ´ng nghá»‡')
    INSERT INTO Category (CategoryName) VALUES (N'CÃ´ng nghá»‡');

IF NOT EXISTS (SELECT 1 FROM Category WHERE CategoryName = N'Kinh táº¿')
    INSERT INTO Category (CategoryName) VALUES (N'Kinh táº¿');

-- Insert sample publishers
IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Kim Äá»“ng')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Kim Äá»“ng');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Tráº»')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Tráº»');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB GiÃ¡o dá»¥c')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB GiÃ¡o dá»¥c');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB Lao Ä‘á»™ng')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB Lao Ä‘á»™ng');

IF NOT EXISTS (SELECT 1 FROM Publisher WHERE PublisherName = N'NXB VÄƒn há»c')
    INSERT INTO Publisher (PublisherName) VALUES (N'NXB VÄƒn há»c');

-- Insert sample roles
IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Admin')
    INSERT INTO Role (RoleName) VALUES (N'Admin');

IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Staff')
    INSERT INTO Role (RoleName) VALUES (N'Staff');

IF NOT EXISTS (SELECT 1 FROM Role WHERE RoleName = N'Librarian')
    INSERT INTO Role (RoleName) VALUES (N'Librarian');

