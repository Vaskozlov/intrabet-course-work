CREATE OR REPLACE FUNCTION create_wallet() RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO wallet (user_id, currency, balance)
    VALUES (NEW.id, 'RUB', 0.0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trig_create_wallet
    AFTER INSERT
    ON application_users
    FOR EACH ROW
EXECUTE FUNCTION create_wallet();
