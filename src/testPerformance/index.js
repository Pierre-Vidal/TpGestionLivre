import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

const livres = [
    { titre: 'Harry Potter', auteur: 'J.K Rowling' },
    { titre: 'Les Misérables', auteur: 'Victor Hugo' },
    { titre: 'Le Petit Prince', auteur: 'Antoine de Saint-Exupéry' },
];

export function creer_livre() {
    const livre = livres[Math.floor(Math.random() * livres.length)];
    const res = http.post(
        'http://localhost:8080/books',
        JSON.stringify(livre),
        { headers: { 'Content-Type': 'application/json' } }
    );
    failureRate.add(res.status !== 201);
}

export function lister_livres() {
    const res = http.get('http://localhost:8080/books');
    failureRate.add(res.status !== 200);
}
