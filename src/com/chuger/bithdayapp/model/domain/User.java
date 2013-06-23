package com.chuger.bithdayapp.model.domain;

import com.chuger.bithdayapp.model.utils.DateTimeUtils;

import java.io.Serializable;
import java.util.Date;

import static com.chuger.bithdayapp.model.utils.StringUtils.isNotEmpty;

/**
 * User: Acer5740
 * Date: 08.02.12
 * Time: 13:32
 */
public class User implements Serializable {

    private static final long serialVersionUID = 2664210841457349984L;
    private Long id;
    private Long facebookId;
    private Long vkontakteId;
    private String googleId;
    private String firstName;
    private String lastName;
    private String additionalName;
    private String title;
    private String picUrl;
    private Date birthday;
    private Boolean yearUnknown;
    private Date updated;
    private Integer yearCount;
    private Integer dayCount;
    // transient
    private String displayName;

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
        return String.format("User{firstName='%s', lastName='%s', bday ='%s'}", firstName, lastName,
                DateTimeUtils.format(birthday));
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

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        if (displayName == null) {
            if (isNotEmpty(title)) {
                displayName = title;
            } else {
                displayName = (isNotEmpty(lastName) ? lastName + " " : "") +
                        (isNotEmpty(firstName) ? firstName + " " : "") +
                        (isNotEmpty(additionalName) ? additionalName : "");
            }
            if (displayName != null) {
                displayName = displayName.trim();
            }
        }
        return displayName;
    }
}
