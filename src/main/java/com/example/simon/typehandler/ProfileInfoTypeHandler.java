package com.example.simon.typehandler;

import com.example.simon.entity.ProfileInfo;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(ProfileInfo.class)
public class ProfileInfoTypeHandler extends JsonTypeHandler<ProfileInfo> {
    public ProfileInfoTypeHandler() {
        super(ProfileInfo.class);
    }
}