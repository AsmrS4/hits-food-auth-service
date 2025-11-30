package admin_service.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "operators")
public class Operator {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    private String fullName;
    @Column(name = "phone", unique = true)
    private String phone;
}
