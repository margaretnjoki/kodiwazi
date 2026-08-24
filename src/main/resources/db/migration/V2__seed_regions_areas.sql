INSERT INTO regions (id, name, created_at, updated_at) VALUES
    (gen_random_uuid(), 'Nairobi', now(), now()),
    (gen_random_uuid(), 'Mombasa', now(), now()),
    (gen_random_uuid(), 'Kisumu', now(), now());

INSERT INTO areas (id, name, region_id, created_at, updated_at)
SELECT gen_random_uuid(), area_name, r.id, now(), now()
FROM regions r
CROSS JOIN (VALUES
    ('Kilimani'),
    ('Westlands'),
    ('Kileleshwa'),
    ('Lavington'),
    ('Kasarani'),
    ('South B'),
    ('South C'),
    ('Karen'),
    ('Ngong Road'),
    ('Roysambu')
) AS t(area_name)
WHERE r.name = 'Nairobi';

INSERT INTO areas (id, name, region_id, created_at, updated_at)
SELECT gen_random_uuid(), area_name, r.id, now(), now()
FROM regions r
CROSS JOIN (VALUES
    ('Nyali'),
    ('Bamburi'),
    ('Tudor'),
    ('Kizingo')
) AS t(area_name)
WHERE r.name = 'Mombasa';

INSERT INTO areas (id, name, region_id, created_at, updated_at)
SELECT gen_random_uuid(), area_name, r.id, now(), now()
FROM regions r
CROSS JOIN (VALUES
    ('Milimani'),
    ('Mamboleo'),
    ('Nyalenda')
) AS t(area_name)
WHERE r.name = 'Kisumu';