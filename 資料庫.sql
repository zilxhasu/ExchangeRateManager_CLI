select * from exchangerate; --查看資料

CREATE TABLE ExchangeRate (
    date CHAR(7) PRIMARY KEY,                    -- 日期作為主鍵                           
    rateNTD DECIMAL(10, 6) NOT NULL,                   -- 匯率 1，最多 10 位數字，其中 6 位是小數位
    rateCNH DECIMAL(10, 6) NOT NULL,                   -- 匯率 2
    rateJPY DECIMAL(10, 6) NOT NULL,                   -- 匯率 3
    rateKRW DECIMAL(10, 6) NOT NULL,                   -- 匯率 4
    rateSGD DECIMAL(10, 6) NOT NULL,                   -- 匯率 5
    rateEUR DECIMAL(10, 6) NOT NULL,                   -- 匯率 6
    rateGBP DECIMAL(10, 6) NOT NULL,                   -- 匯率 7
    rateAUD DECIMAL(10, 6) NOT NULL,                   -- 匯率 8
    );

DELETE from exchangerate --刪除資料














-- 刪除 CurrencyPair 表格
DROP TABLE exchangerate;

-- 刪除 Currency 表格
DROP TABLE Currency;



CREATE TABLE CurrencyPair (
    pair_id INT IDENTITY(1,1) PRIMARY KEY,         -- 自動增長的主鍵
    base_currency_id INT NOT NULL,                  -- 基本貨幣的 `currency_id`
    target_currency_id INT NOT NULL,                -- 目標貨幣的 `currency_id`
    FOREIGN KEY (base_currency_id) REFERENCES Currency(currency_id),  -- 外鍵：參照 `Currency` 表的 `currency_id`
    FOREIGN KEY (target_currency_id) REFERENCES Currency(currency_id), -- 外鍵：參照 `Currency` 表的 `currency_id`
    CONSTRAINT UC_CurrencyPair UNIQUE (base_currency_id, target_currency_id)  -- 保證每個貨幣對唯一
);

CREATE TABLE ExchangeRateHistory (
    history_id INT IDENTITY(1,1) PRIMARY KEY,      -- 自動增長的主鍵
    pair_id INT NOT NULL,                           -- 參照 `CurrencyPair` 表的 `pair_id`
    update_date DATE NOT NULL,                      -- 匯率更新日期
    rate_value DECIMAL(10, 6) NOT NULL,             -- 匯率值
    FOREIGN KEY (pair_id) REFERENCES CurrencyPair(pair_id)  -- 外鍵：參照 `CurrencyPair` 表的 `pair_id`
);






CREATE TABLE Currency (
    currency_id INT IDENTITY(1,1) PRIMARY KEY,  -- 使用 IDENTITY 代替 AUTO_INCREMENT
    currency_code VARCHAR(3) NOT NULL,          -- 貨幣代碼（例如：USD, EUR, JPY）
    currency_name VARCHAR(100) NOT NULL,        -- 貨幣名稱（例如：美元，歐元，日元）
    CONSTRAINT UQ_currency_code UNIQUE (currency_code)  -- 使用 CONSTRAINT 定義唯一約束
);

CREATE TABLE CurrencyPair (
    pair_id INT IDENTITY(1,1) PRIMARY KEY,        -- 使用 IDENTITY 代替 AUTO_INCREMENT
    base_currency_id INT NOT NULL,                  -- 基本貨幣（例如：USD）
    target_currency_id INT NOT NULL,                -- 目標貨幣（例如：TWD, CNY, JPY 等）
    FOREIGN KEY (base_currency_id) REFERENCES Currency(currency_id),  -- 基本貨幣的外鍵
    FOREIGN KEY (target_currency_id) REFERENCES Currency(currency_id), -- 目標貨幣的外鍵
    CONSTRAINT UQ_currency_pair UNIQUE (base_currency_id, target_currency_id)   -- 保證每對貨幣對唯一
);

CREATE TABLE ExchangeRate (
    month VARCHAR(7) NOT NULL,                      -- 匯率資料的年月，儲存格式為 'YYYY-MM'
    pair_id INT NOT NULL,                            -- 貨幣對的 ID（例如：USD/TWD）
    rate DECIMAL(10, 6) NOT NULL,                    -- 匯率數值
    PRIMARY KEY (month, pair_id),                    -- 以月和貨幣對作為主鍵
    FOREIGN KEY (pair_id) REFERENCES CurrencyPair(pair_id)  -- 外鍵：指向貨幣對表
);

-- 插入 Currency 表格的資料
INSERT INTO Currency (currency_code, currency_name)
VALUES 
('USD', '美元'),
('TWD', '新台幣'),
('CNY', '人民幣'),
('JPY', '日圓'),
('KRW', '韓元'),
('SGD', '新加坡元'),
('EUR', '歐元'),
('GBP', '英鎊'),
('AUD', '澳幣');

select * FROM CurrencyPair

-- 插入 USD/TWD (新台幣)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'TWD');

-- 插入 USD/CNY (人民幣)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'CNY');

-- 插入 USD/JPY (日圓)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'JPY');

-- 插入 USD/KRW (韓元)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'KRW');

-- 插入 USD/SGD (新加坡元)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'SGD');

-- 插入 USD/EUR (歐元)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'EUR');

-- 插入 USD/GBP (英鎊)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'GBP');

-- 插入 USD/AUD (澳幣)
INSERT INTO CurrencyPair (base_currency_id, target_currency_id)
SELECT 
    (SELECT currency_id FROM Currency WHERE currency_code = 'USD'),
    (SELECT currency_id FROM Currency WHERE currency_code = 'AUD');
