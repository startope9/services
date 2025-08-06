    package user_registration_service.repository;

    import user_registration_service.model.User;
    import org.springframework.data.mongodb.repository.MongoRepository;

    import java.util.Optional;

    public interface UserRepository extends MongoRepository<User, String> {
        Optional<User> findByEmail(String email);
    }
