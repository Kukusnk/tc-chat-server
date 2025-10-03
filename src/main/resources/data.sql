INSERT INTO topic (name) VALUES
    ('Arts & Culture'),
    ('Technology & Gadgets'),
    ('Gaming'),
    ('Travel'),
    ('Food'),
    ('Sports'),
    ('Hobbies & DIY'),
    ('Lifestyle & Wellbeing'),
    ('Science'),
    ('Education & Careers'),
    ('Finance')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role (name) VALUES ('USER'), ('ADMIN') ON CONFLICT (name) DO NOTHING;