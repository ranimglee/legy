package user.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import user.domain.model.*;
import user.domain.repository.UserRepository;
import user.infrastructure.mapper.*;
import user.infrastructure.persistence.entities.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

        private final MongoUserRepository mongoRepo;
        private final RedisTemplate<String, String> redisTemplate;

        public UserRepositoryImpl(
                MongoUserRepository mongoRepo,
                @Qualifier("connectedRedisTemplate") RedisTemplate<String, String> redisTemplate
        ) {
            this.mongoRepo = mongoRepo;
            this.redisTemplate = redisTemplate;
        }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return mongoRepo.findByEmail(email).map(this::mapToDomain);
    }


    @Override
    public Optional<UserEntity> findById(String userId) {
        return mongoRepo.findById(userId).map(this::mapToDomain);
    }


    @Override
    public UserEntity saveUser(UserEntity user) {
        MongoUserEntity mongo = mapToMongo(user);
        return mapToDomain(mongoRepo.save(mongo));
    }

    @Override
    public ClientEntity saveClient(ClientEntity client) {
        try {
            MongoClientEntity mongo = ClientMapper.toMongo(client);
            return ClientMapper.toDomain(mongoRepo.save(mongo));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveClient", e);
        }
    }

    public LivreurEntity saveLivreur(LivreurEntity livreur) {
        try {
            MongoLivreurEntity mongo = LivreurMapper.toMongo(livreur);
            MongoLivreurEntity savedMongo = mongoRepo.save(mongo);
            LivreurEntity savedLivreur = LivreurMapper.toDomain(savedMongo);

            return savedLivreur;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveLivreur", e);
        }
    }


    @Override
    public RestaurantManagerEntity saveRestaurantManager(RestaurantManagerEntity manager) {
        try {
            MongoRestaurantManagerEntity mongo = RestaurantManagerMapper.toMongo(manager);
            return RestaurantManagerMapper.toDomain(mongoRepo.save(mongo));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveUser", e);
        }
    }

    @Override
    public ModerateurEntity saveModerateur(ModerateurEntity moderateur) {
        try {
            MongoModerateurEntity mongo = ModerateurMapper.toMongo(moderateur);
            return ModerateurMapper.toDomain(mongoRepo.save(mongo));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveUser", e);
        }
    }

    @Override
    public FinancierEntity saveFinancier(FinancierEntity financier) {
        try {
            MongoFinancierEntity mongo = FinancierMapper.toMongo(financier);
            return FinancierMapper.toDomain(mongoRepo.save(mongo));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveUser", e);
        }
    }



    @Override
    public List<ClientEntity> findByIdIn(List<String> userIds) {
        return mongoRepo.findByIdIn(userIds).stream()
                .map(mongo -> {
                    try {
                        return ClientMapper.toDomain(mongo);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException("Mapping error", e);
                    }
                })
                .collect(Collectors.toList());
    }



    @Override
    public List<LivreurEntity> findAvailableLivreurs() {
        Set<String> connectedIds = redisTemplate.opsForSet().members("connected_livreurs");

        if (connectedIds == null || connectedIds.isEmpty()) {
            return List.of(); // No connected livreurs
        }

        // Fetch users and filter only livreurs
        List<MongoUserEntity> mongoUsers = mongoRepo.findAllById(connectedIds);
        System.out.println("=========================>");
        return mongoUsers.stream()
                .filter(user -> user instanceof MongoLivreurEntity)
                .map(user -> {
                    try {
                        return LivreurMapper.toDomain((MongoLivreurEntity) user);
                    } catch (Exception e) {
                        throw new RuntimeException("Mapping failed for livreur", e);
                    }
                })
                .toList();
    }




    public boolean existsByPhoneNumber(String phoneNumber) {
        return mongoRepo.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return mongoRepo.findByUsername(username).map(this::mapToDomain);
    }

    private MongoUserEntity mapToMongo(UserEntity user) {
        try {
            if (user instanceof ClientEntity c) {
                return ClientMapper.toMongo(c);
            } else if (user instanceof LivreurEntity l) {
                return LivreurMapper.toMongo(l);
            } else if (user instanceof RestaurantManagerEntity r) {
                return RestaurantManagerMapper.toMongo(r);
            } else if (user instanceof ModerateurEntity m) {
                return ModerateurMapper.toMongo(m);
            } else if (user instanceof FinancierEntity m) {
                return FinancierMapper.toMongo(m);
            } else {
                throw new IllegalArgumentException("Unknown UserEntity subclass");
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveUser", e);
        }
    }


    private UserEntity mapToDomain(MongoUserEntity mongo) {
        try {
            if (mongo instanceof MongoClientEntity mongoClient) {
                return ClientMapper.toDomain(mongoClient);
            } else if (mongo instanceof MongoLivreurEntity mongoLivreur) {
                return LivreurMapper.toDomain(mongoLivreur);
            } else if (mongo instanceof MongoRestaurantManagerEntity mongoManager) {
                return RestaurantManagerMapper.toDomain(mongoManager);
            } else if (mongo instanceof MongoModerateurEntity mongoMod) {
                return ModerateurMapper.toDomain(mongoMod);
            } else if (mongo instanceof MongoFinancierEntity mongoMod) {
                return FinancierMapper.toDomain(mongoMod);
            } else {
                throw new IllegalArgumentException("Unknown MongoUserEntity type: " + mongo.getClass().getSimpleName());
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Mapping failed during saveUser", e);
        }
    }

    @Override
    public Optional<ClientEntity> findClientById(String userId) {
        
        return mongoRepo.findById(userId)
                .filter(mongoUser -> mongoUser instanceof MongoClientEntity)
                .map(mongoUser -> {
                    MongoClientEntity mongoClient = (MongoClientEntity) mongoUser;
                    try {
                        return ClientMapper.toDomain(mongoClient);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
    @Override
    public List<UserEntity> findAllNonModerateurUsers() {
        List<MongoUserEntity> users = mongoRepo.findAllExceptModerateurs();
        return users.stream().map(this::mapToDomain).toList();

    }

    @Override
    public Map<String, Long> countConnectedUsersByRole() {
        Map<String, Long> counts = new HashMap<>();

        counts.put("clients", getSize("connected_clients"));
        counts.put("livreurs", getSize("connected_livreurs"));
        counts.put("managers", getSize("connected_restaurant_managers"));
        counts.put("moderateurs", getSize("connected_moderateurs"));
        counts.put("financiers", getSize("connected_financiers"));

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        counts.put("total", total);

        return counts;
    }

    private long getSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0;
    }


    @Override
    public long countActiveUsers() {
        return mongoRepo.countActiveUsers();
    }

    @Override
    public void deleteById(String id) {
        mongoRepo.deleteById(id);
    }
    @Override
    public void updateStatus(String userId, Status status) {
        mongoRepo.findById(userId).ifPresent(user -> {
            user.setStatus(status);
            mongoRepo.save(user);
        });
    }


    @Override
    public List<LivreurEntity> findByIsAssignedFalse() {
        List<MongoLivreurEntity> unassignedLivreurs = mongoRepo.findByIsAssignedFalseOrNullAndStatus(Status.ACTIVE);

        return unassignedLivreurs.stream()
                .map(mongoLivreur -> {
                    try {
                        return LivreurMapper.toDomain(mongoLivreur);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException("Failed to map unassigned livreur", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LivreurEntity> findByIdInAndStatus(List<String> livreurIds, Status status) {
        List<MongoLivreurEntity> mongoLivreurs = mongoRepo.aggregateLivreursByIdInAndStatus(livreurIds, status);

        return mongoLivreurs.stream()
                .map(mongoLivreur -> {
                    try {
                        return LivreurMapper.toDomain(mongoLivreur);
                    } catch (Exception e) {
                        log.error("❌ Mapping failed for livreur {}: {}", mongoLivreur.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    @Override
    public void updateFcmToken(String userId, String fcmToken) {
        mongoRepo.findById(userId).ifPresent(user -> {
            user.setFcmToken(fcmToken);
            mongoRepo.save(user);
        });
    }

    @Override
    public List<LivreurEntity> findAllLivreurs() {
        List<MongoUserEntity> users = mongoRepo.findAll();

        return users.stream()
                .filter(user -> user instanceof MongoLivreurEntity)
                .map(user -> {
                    try {
                        return LivreurMapper.toDomain((MongoLivreurEntity) user);
                    } catch (Exception e) {
                        log.error("❌ Mapping failed for livreur {}: {}", user.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public long countClientsByCreatedAtBetween(Instant start, Instant end) {
        Long count = mongoRepo.countByCreatedAtBetweenAndClassName(
                start, end, MongoClientEntity.class.getName());
        return count != null ? count : 0L;
    }



    @Override
    public long countDriversByCreatedAtBetween(Instant start, Instant end) {
        Long count = mongoRepo.countByCreatedAtBetweenAndClassName(
                start, end, MongoLivreurEntity.class.getName());
        return count != null ? count : 0L;
    }


}