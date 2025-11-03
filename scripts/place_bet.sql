CREATE OR REPLACE FUNCTION place_bet(
    p_user_id INT, 
    p_event_id INT, 
    p_amount DECIMAL, 
    p_outcome_id INT
) RETURNS VOID AS $$
BEGIN
    IF (SELECT balance FROM wallets WHERE user_id = p_user_id) < p_amount THEN
        RAISE EXCEPTION 'Insufficient balance';
    END IF;
    INSERT INTO bets (user_id, event_id, amount, outcome_id) 
    VALUES (p_user_id, p_event_id, p_amount, p_outcome_id);
    -- Триггер обработает обновление баланса
END;
$$ LANGUAGE plpgsql;
