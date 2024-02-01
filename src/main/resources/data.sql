DELETE FROM transaction;
INSERT INTO transaction (data, created_at) VALUES
  ('{ "amount": 100, "reference": "BM_2023_101" }', NOW()),
  ('{ "amount": 3333, "reference": "", "sender": "Bankmonitor" }', NOW()),
  ('{ "amount": -100, "reference": "BM_2023_101_BACK", "reason": "duplicate" }', NOW()),
  ('{ "amount": 12345, "reference": "BM_2023_105" }', NOW()),
  ('{ "amount": 54321, "sender": "Bankmonitor", "recipient": "John Doe" }', NOW());