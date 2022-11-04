package com.ericgrandt.totaleconomy.data.dto;

import java.sql.Timestamp;

public class AccountDto {
    private final String id;
    private final Timestamp created;

    public AccountDto(String id, Timestamp created) {
        this.id = id;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public Timestamp getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AccountDto that = (AccountDto) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return created.equals(that.created);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }
}