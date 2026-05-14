-- public.franchise definition

CREATE TABLE public.franchise (
    id bigserial NOT NULL,
    "name" varchar(255) NOT NULL,
    CONSTRAINT franchise_name_key UNIQUE (name),
    CONSTRAINT franchise_pkey PRIMARY KEY (id)
);


-- public.branch definition

CREATE TABLE public.branch (
    id bigserial NOT NULL,
    "name" varchar(255) NOT NULL,
    franchise_id int8 NOT NULL,
    CONSTRAINT branch_pkey PRIMARY KEY (id),
    CONSTRAINT unique_branch_name_per_franchise UNIQUE (name, franchise_id),
    CONSTRAINT branch_franchise_id_fkey FOREIGN KEY (franchise_id) REFERENCES public.franchise(id) ON DELETE CASCADE
);


-- public.product definition

CREATE TABLE public.product (
    id bigserial NOT NULL,
    "name" varchar(255) NOT NULL,
    stock int4 NOT NULL,
    branch_id int8 NOT NULL,
    status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
    CONSTRAINT product_pkey PRIMARY KEY (id),
    CONSTRAINT unique_product_name_per_branch UNIQUE (name, branch_id),
    CONSTRAINT product_branch_id_fkey FOREIGN KEY (branch_id) REFERENCES public.branch(id) ON DELETE CASCADE
);
