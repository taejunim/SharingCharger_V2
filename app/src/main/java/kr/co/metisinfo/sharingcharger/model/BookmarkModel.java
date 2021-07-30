package kr.co.metisinfo.sharingcharger.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import kr.co.metisinfo.sharingcharger.base.DBConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor      // 생성자 자동생성
@Entity(tableName = DBConstants.TABLE_BOOKMARK)
public class BookmarkModel {


    @PrimaryKey(autoGenerate = true) // 필요없는 값
    public int pk;

    public int userId;
    public int chargerId;


    @Ignore
    public boolean isClick = false;

}
