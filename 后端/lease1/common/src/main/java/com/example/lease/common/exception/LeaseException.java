package com.example.lease.common.exception;

import com.example.lease.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class LeaseException extends RuntimeException{
  private Integer code;

  public LeaseException(ResultCodeEnum resultCodeEnum){
      super(resultCodeEnum.getMessage());
      this.code=resultCodeEnum.getCode();
  }
}
