package filter.load.thrift.Base;

/**
 * 操作类型
 */
public enum AlohaType implements org.apache.thrift.TEnum {
  Aloha(0),
  AlohaGet(1);

  private final int value;

  AlohaType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static AlohaType findByValue(int value) { 
    switch (value) {
      case 0:
        return Aloha;
      case 1:
        return AlohaGet;
      default:
        return null;
    }
  }
}