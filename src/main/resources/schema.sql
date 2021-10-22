DROP TRIGGER IF EXISTS on_add_passenger^; 

CREATE TRIGGER on_add_passenger
AFTER INSERT ON passenger
FOR EACH ROW
UPDATE flight f
LEFT JOIN flight_bookings fb ON f.id = fb.flight_id
LEFT JOIN booking b ON b.id = fb.booking_id
SET f.reserved_seats = f.reserved_seats+1
WHERE b.id = NEW.booking_id^;
