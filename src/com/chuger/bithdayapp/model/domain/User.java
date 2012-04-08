package com.chuger.bithdayapp.model.domain;

import com.chuger.bithdayapp.model.utils.DateTimeUtils;

import java.util.Date;

/**
 * User: Acer5740
 * Date: 08.02.12
 * Time: 13:32
 */
public class User {

    private Long id;
    private Long facebookId;
    private Long vkontakteId;
    private String firstName;
    private String lastName;
    private String picUrl;
    private Date birthday;
    private Boolean yearUnknown;
    private Date updated;
    private Integer yearCount;
    private Integer dayCount;

    public User() {
    }

    public Long getVkontakteId() {
        return vkontakteId;
    }

    public void setVkontakteId(Long vkontakteId) {
        this.vkontakteId = vkontakteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(final Long facebookId) {
        this.facebookId = facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(final String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return String.format("User{firstName='%s', lastName='%s', bday ='%s'}", firstName, lastName, DateTimeUtils.format(birthday));
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getYearUnknown() {
        return Boolean.TRUE.equals(yearUnknown);
    }

    public void setYearUnknown(final Boolean yearUnknown) {
        this.yearUnknown = yearUnknown;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(final Date updated) {
        this.updated = updated;
    }

    public Integer getYearCount() {
        return yearCount;
    }

    public void setYearCount(final Integer yearCount) {
        this.yearCount = yearCount;
    }

    public Integer getDayCount() {
        return dayCount;
    }

    public void setDayCount(final Integer dayCount) {
        this.dayCount = dayCount;
    }
}
