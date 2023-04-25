package me.jics;


import lombok.Data;
import lombok.Value;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;

import java.io.Serializable;


@Data
@Value
public class UserData implements Serializable {
    String name;
    String lastname;
}
